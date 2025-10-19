package org.zipcoder.utilsmod.commands;

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
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.zipcoder.utilsmod.UtilsMod;
import org.zipcoder.utilsmod.config.PreInitConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

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

        event.getDispatcher().register(
                Commands.literal(NAMESPACE)
                        .then(Commands.literal("pos").requires(source -> source.hasPermission(2))
                                .then(Commands.argument("target", EntityArgument.player()) // /neutron pos <target>
                                        .executes(ctx -> {
                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
                                            double x = target.getX();
                                            double y = target.getY();
                                            double z = target.getZ();
                                            ctx.getSource().sendSuccess(() -> Component.literal(
                                                    String.format("%s's position → X: %.2f  Y: %.2f  Z: %.2f",
                                                            target.getName().getString(), x, y, z)), false);
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
                        .then(Commands.literal("kill").requires(source -> source.hasPermission(2))
                                .then(Commands.literal("near")
                                        .executes(context -> {
                                            executeParsedCommand(context.getSource(), "/kill @e[type=!player,distance=..10]");
                                            return Command.SINGLE_SUCCESS;
                                        })
                                )
                        )
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

//                .then(Commands.literal("listAddedCommands").requires(source -> source.hasPermission(2))
//                        .executes(context -> {
//                            StringBuilder sb = new StringBuilder();
//                            if (UtilsMod.CONFIG.exposeOPCommands.length == 0) {
//                                sb.append("No custom commands");
//                            } else {
//                                for (PreInitConfig.ExposedOPCommand op : UtilsMod.CONFIG.exposeOPCommands) {
//                                    if (op == null) continue;
//                                    sb.append("Keyword: \"").append(op.keyword).append("\" Executes: ")
//                                            .append(op.command).append(" (allow arguments: ").append(op.allowArguments).append(")\n");
//                                }
//                            }
//                            context.getSource().sendSuccess(() -> Component.literal(sb.toString()), false);
//                            return Command.SINGLE_SUCCESS;
//                        })
//                )

//        for (PreInitConfig.ExposedOPCommand op : UtilsMod.CONFIG.exposeOPCommands) {
//            if (op == null) continue;
//
//            // Create the keyword literal first
//            var keywordLiteral = Commands.literal(op.keyword);
//
//            // If arguments are allowed, add the argument node to keywordLiteral
//            if (op.allowArguments) {
//                keywordLiteral = keywordLiteral.then(
//                        Commands.argument("args", StringArgumentType.greedyString())
//                                .executes(context -> {
//                                    String extraArgs = StringArgumentType.getString(context, "args");
//                                    String fullCommand = op.command + " " + extraArgs;
//                                    return executeParsedCommandOP(context.getSource(), fullCommand, false);
//                                })
//                );
//            }
//
//            // Add execute without arguments to keywordLiteral
//            keywordLiteral = keywordLiteral.executes(context -> {
//                return executeParsedCommandOP(context.getSource(), op.command, false);
//            });
//
//            // Build the full command tree with namespace and "exposed"
//            var literal = Commands.literal(NAMESPACE)
//                    .then(Commands.literal("commands")
//                            .then(keywordLiteral)
//                    );
//
//            dispatcher.register(literal);
//        }


        /**
         * Crash/ overload
         */
        if (UtilsMod.CONFIG.crashCommands) {
            event.getDispatcher().register(Commands.literal(NAMESPACE)
                    .requires(source -> source.hasPermission(2))

                    .then(Commands.literal("crash"))
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