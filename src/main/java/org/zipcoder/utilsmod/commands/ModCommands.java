package org.zipcoder.utilsmod.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.zipcoder.utilsmod.UtilsMod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class ModCommands {

    public final static String NAMESPACE = "neutron";

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
                .requires(source -> source.hasPermission(2))


                .then(Commands.literal("pos") // /neutron pos
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
                .then(Commands.literal("kill")
                        .then(Commands.literal("near")
                                .executes(context -> {
                                    executeParsedCommand(context.getSource(), "/kill @e[type=!player,distance=..10]");
                                    return Command.SINGLE_SUCCESS;
                                })
                        )
                )
        );

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
}