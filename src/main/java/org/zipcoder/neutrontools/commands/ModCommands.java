package org.zipcoder.neutrontools.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.hypherionmc.morecreativetabs.ModConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.zipcoder.neutrontools.NeutronTools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModCommands {

    public final static String NAMESPACE = "neutron";

    public static int executeParsedCommandOP(CommandSourceStack originalSource, String command, boolean redirectOutput) {
        MinecraftServer server = originalSource.getServer();
        var dispatcher = server.getCommands().getDispatcher();

        // Remove leading slash
        if (command.startsWith("/")) {
            command = command.substring(1);
        }


        try {
            CommandSourceStack serverSource;

            if (redirectOutput) {
                serverSource = new CommandSourceStack(
                        originalSource.getPlayer(), // entity
                        originalSource.getPlayer().position(), // position
                        originalSource.getPlayer().getRotationVector(), // rotation

                        server.getLevel(originalSource.getPlayer().level().dimension()).getServer()
                                .getLevel(originalSource.getPlayer().level().dimension()), // server level access

                        4, // permission level (OP)
                        originalSource.getPlayer().getName().getString(), // name
                        originalSource.getPlayer().getDisplayName(), // display name
                        server, // server
                        originalSource.getPlayer() // entity again
                ).withPermission(4)
                        .withSuppressedOutput(); // ensure messages show
            } else {
                serverSource = server.createCommandSourceStack()
                        .withPermission(4) // Full OP level
                        .withSuppressedOutput();
            }

            ParseResults<CommandSourceStack> parseResults = dispatcher.parse(command, serverSource);
            return dispatcher.execute(parseResults);
        } catch (CommandSyntaxException e) {
            originalSource.sendFailure(Component.literal("Error executing command: " + e.getMessage()));
            return 0;
        } catch (Exception e) {
            originalSource.sendFailure(Component.literal("Error executing command: " + e.getMessage()));
            return 0;
        }
    }


    /**
     * Parses and executes a command string using the given source.
     *
     * @param source  The command source.
     * @param command The command to execute (without a leading '/').
     * @return The number of affected entities or a result code.
     */
    public static int executeParsedCommand(CommandSourceStack source, String command) {
        // Use the server's command dispatcher
        MinecraftServer server = source.getServer();
        var dispatcher = server.getCommands().getDispatcher();

        // Parse the command string (if a leading slash exists, remove it)
        if (command.startsWith("/")) {
            command = command.substring(1);
        }
        ParseResults<CommandSourceStack> parseResults = dispatcher.parse(command, source);
        try {
            // Execute the parsed command and return the result
            return dispatcher.execute(parseResults);
        } catch (CommandSyntaxException e) {
            source.sendFailure(Component.literal("Error executing command: " + e.getMessage()));
            return 0;
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        ListAllCommand.register(dispatcher);

        event.getDispatcher().register(Commands.literal(NAMESPACE)
                .then(Commands.literal("kill")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("near")
                                .executes(context -> {
                                    executeParsedCommand(context.getSource(), "/kill @e[type=!player,distance=..10]");
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
                .then(Commands.literal("locate")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())

                                .executes(ctx -> {
                                    ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
                                    double x = player.getX();
                                    double y = player.getY();
                                    double z = player.getZ();
                                    ctx.getSource().sendSuccess(() -> Component.literal(
                                            String.format("%s's position → X: %.2f  Y: %.2f  Z: %.2f",
                                                    player.getName().getString(), x, y, z)), false);
                                    return Command.SINGLE_SUCCESS;
                                })
                        ))
                .then(Commands.literal("ping")
                        .executes(ctx -> {
                            MinecraftServer server = ctx.getSource().getServer();
                            Collection<ServerPlayer> players = server.getPlayerList().getPlayers();
                            for (ServerPlayer player : players) {
                                ping(server, player, ctx.getSource().getPlayer());
                            }
                            return 1;//1=success
                        })
                        .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("username", StringArgumentType.word())
                                .suggests(usernameSuggestions())
                                .executes(ctx -> {
                                    String targetName = StringArgumentType.getString(ctx, "username");
                                    MinecraftServer server = ctx.getSource().getServer();
                                    if (!targetName.isEmpty()) {
                                        ServerPlayer asking = server.getPlayerList().getPlayerByName(targetName);
                                        if (asking != null) {
                                            ping(server, asking, ctx.getSource().getPlayer());
                                            return 1;
                                        }
                                    }
                                    return 0;
                                })
                        ))
        );

        /**
         * Crash/ overload
         */
        if (NeutronTools.CONFIG.crashCommands) {
            event.getDispatcher().register(Commands.literal(NAMESPACE)
                    .then(Commands.literal("crash"))
                    .requires(source -> source.hasPermission(2))
                    .executes(context -> {

                        (new Thread(() -> {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            System.exit(1);
                            Runtime.getRuntime().exit(1);
                        })).start();
                        context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Crash command executed"), false);
                        return Command.SINGLE_SUCCESS;
                    })
                    .then(Commands.literal("overload"))
                    .requires(source -> source.hasPermission(2))
                    .executes(context -> {
                        //Create 10 threads
                        for (int i = 0; i < 100; i++) {
                            (new Thread(() -> {
                                List<int[]> memoryFiller = new ArrayList<>();
                                while (true) {
                                    memoryFiller.add(new int[1000000]); // Allocates large arrays continuously
                                }
                            })).start();
                        }

                        return Command.SINGLE_SUCCESS;
                    })
            );
        }
    }

    private static void ping(MinecraftServer server, ServerPlayer asking, ServerPlayer user) {
        System.out.println("Pinging " + asking.getName().getString());
//        int out = executeParsedCommandOP("spark ping --player " + asking.getName().getString(),true );
        int ping = asking.connection.player.latency;
        ChatFormatting color = ChatFormatting.GREEN;
        if (ping > 200) {
            color = ChatFormatting.RED;
        } else if (ping > 100) {
            color = ChatFormatting.YELLOW;
        }

        ChatFormatting finalColor = color;
        user.sendSystemMessage(
                Component.literal("Ping for \"" + asking.getName().getString() + "\": ")
                        .append(Component.literal(ping + "ms").
                                withStyle(style -> style
                                        .withColor(finalColor) // color it differently
                                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,
                                                asking.getName().getString() + " has " + ping + "ms ping"))
                                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Copy")))
                                )
                        )

        );
    }

    private static SuggestionProvider<CommandSourceStack> usernameSuggestions() {
        return (CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) -> {
            List<String> suggestions = new ArrayList<>();
            Collection<ServerPlayer> players = context.getSource().getServer().getPlayerList().getPlayers();
            players.forEach(player -> suggestions.add(player.getName().getString()));
            return SharedSuggestionProvider.suggest(suggestions, builder);
        };
    }

}