package org.zipcoder.utilsmod;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class ModCommands {

    private static boolean listBlocksToFile(File saveFile) {
        System.out.println("Saving block list to: " + saveFile.getAbsolutePath());
        try (FileWriter writer = new FileWriter(saveFile)) {
            for (ResourceLocation id : BuiltInRegistries.BLOCK.keySet()) {
//                Block item = BuiltInRegistries.BLOCK.get(id);
                writer.write(id.toString() + "\n");
            }
            System.out.println("Saved block list to: " + saveFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save block list: " + e.getMessage());
        }
        return false;
    }


    private static boolean listCreativeModeTabsToFile(File saveFile) {
        System.out.println("Saving creative mode tab list to: " + saveFile.getAbsolutePath());
        try (FileWriter writer = new FileWriter(saveFile)) {
            for (ResourceLocation id : BuiltInRegistries.CREATIVE_MODE_TAB.keySet()) {
                writer.write(id.toString() + "\n");
            }
            System.out.println("Saved creative mode tab list to: " + saveFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save creative mode tab list: " + e.getMessage());
        }
        return false;
    }

    private static boolean listEntitiesToFile(File saveFile) {
        System.out.println("Saving block list to: " + saveFile.getAbsolutePath());
        try (FileWriter writer = new FileWriter(saveFile)) {
            for (ResourceLocation id : BuiltInRegistries.ENTITY_TYPE.keySet()) {
//                EntityType item = BuiltInRegistries.ENTITY_TYPE.get(id);
                writer.write(id.toString() + "\n");
            }
            System.out.println("Saved block list to: " + saveFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save block list: " + e.getMessage());
        }
        return false;
    }

    private static boolean listItemsToFile(File saveFile) {
        System.out.println("Saving item list to: " + saveFile.getAbsolutePath());
        try (FileWriter writer = new FileWriter(saveFile)) {
            for (ResourceLocation id : BuiltInRegistries.ITEM.keySet()) {
//                Item item = BuiltInRegistries.ITEM.get(id);
                writer.write(id.toString() + "\n");
            }
            System.out.println("Saved item list to: " + saveFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save item list: " + e.getMessage());
        }
        return false;
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

        /**
         * List all items
         */
        dispatcher.register(Commands.literal("listall")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("items")
                        .executes(context -> {
                            File savePath = new File("items_list.txt");
                            if (listItemsToFile(savePath))
                                context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("List saved to: " + savePath.getAbsolutePath()), true);
                            else
                                context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Failed to save list (path: " + savePath.getAbsolutePath() + ")!"), true);
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("blocks")
                        .executes(context -> {
                            File savePath = new File("blocks_list.txt");
                            if (listBlocksToFile(savePath))
                                context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("List saved to: " + savePath.getAbsolutePath()), true);
                            else
                                context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Failed to save list (path: " + savePath.getAbsolutePath() + ")!"), true);
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("entities")
                        .executes(context -> {
                            File savePath = new File("entities_list.txt");
                            if (listEntitiesToFile(savePath))
                                context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("List saved to: " + savePath.getAbsolutePath()), true);
                            else
                                context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Failed to save list (path: " + savePath.getAbsolutePath() + ")!"), true);
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("creativetabs")
                        .executes(context -> {
                            File savePath = new File("creative_mode_tabs.txt");
                            if (listCreativeModeTabsToFile(savePath))
                                context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("List saved to: " + savePath.getAbsolutePath()), true);
                            else
                                context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Failed to save list (path: " + savePath.getAbsolutePath() + ")!"), true);
                            return Command.SINGLE_SUCCESS;
                        }))
        );

        /**
         * Crash/ overload
         */
        dispatcher.register(Commands.literal("crash")
                .requires(source -> source.hasPermission(2))  // Only players with permission level 2 or higher see this command
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
        );

        dispatcher.register(Commands.literal("overload")
                .requires(source -> source.hasPermission(2))  // Only players with permission level 2 or higher see this command
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
                }));

        /**
         * Kill
         */
        dispatcher.register(Commands.literal("kill")
                .requires(source -> source.hasPermission(2))  // Only players with permission level 2 or higher see this command
                .then(Commands.literal("near")
                        .executes(context -> {
                            executeParsedCommand(context.getSource(), "/kill @e[type=!player,distance=..10]");
                            return Command.SINGLE_SUCCESS;
                        })));
    }
}