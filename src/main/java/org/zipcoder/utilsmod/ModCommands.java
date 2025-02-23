package org.zipcoder.utilsmod;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
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
                Block item = BuiltInRegistries.BLOCK.get(id);
                writer.write(id.toString() + "\n");
            }
            System.out.println("Saved block list to: " + saveFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save block list: " + e.getMessage());
        }
        return false;
    }

    private static boolean listEntitiesToFile(File saveFile) {
        System.out.println("Saving block list to: " + saveFile.getAbsolutePath());
        try (FileWriter writer = new FileWriter(saveFile)) {
            for (ResourceLocation id : BuiltInRegistries.ENTITY_TYPE.keySet()) {
                EntityType item = BuiltInRegistries.ENTITY_TYPE.get(id);
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
                Item item = BuiltInRegistries.ITEM.get(id);
                writer.write(id.toString() + "\n");
            }
            System.out.println("Saved item list to: " + saveFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save item list: " + e.getMessage());
        }
        return false;
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Register a simple command: /listitems
        dispatcher.register(Commands.literal("listitems")
                .executes(context -> {
                    File savePath = new File("items_list.txt");
                    if (listItemsToFile(savePath))
                        context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Item list saved to: " + savePath.getAbsolutePath()), true);
                    else
                        context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Failed to save item list (path: " + savePath.getAbsolutePath() + ")!"), true);
                    return Command.SINGLE_SUCCESS;
                })
        );

        dispatcher.register(Commands.literal("listblocks")
                .executes(context -> {
                    File savePath = new File("blocks_list.txt");
                    if (listBlocksToFile(savePath))
                        context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Block list saved to: " + savePath.getAbsolutePath()), true);
                    else
                        context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Failed to save block list (path: " + savePath.getAbsolutePath() + ")!"), true);
                    return Command.SINGLE_SUCCESS;
                })
        );

        dispatcher.register(Commands.literal("listentities")
                .executes(context -> {
                    File savePath = new File("entities_list.txt");
                    if (listEntitiesToFile(savePath))
                        context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Entity list saved to: " + savePath.getAbsolutePath()), true);
                    else
                        context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Failed to save entity list (path: " + savePath.getAbsolutePath() + ")!"), true);
                    return Command.SINGLE_SUCCESS;
                })
        );

//        // Example of a command with an argument: /greet <name>
//        dispatcher.register(Commands.literal("greet")
//                .then(Commands.argument("name", StringArgumentType.string())
//                        .executes(context -> {
//                            String name = StringArgumentType.getString(context, "name");
//                            context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Hello, " + name + "!"), false);
//                            return Command.SINGLE_SUCCESS;
//                        })
//                )
//        );

        dispatcher.register(Commands.literal("crash")
                        .executes(context -> {
                            throw new NullPointerException("Intentional crash triggered by command.");
//                    System.out.println(10 / 0);
//                    context.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("crash: " + 10 / 0), false);
//                    return Command.SINGLE_SUCCESS;
                        })
        );

        dispatcher.register(Commands.literal("crashmemory")
                .executes(context -> {
                    List<int[]> memoryFiller = new ArrayList<>();
                    while (true) {
                        memoryFiller.add(new int[1000000]); // Allocates large arrays continuously
                    }
                })
        );

    }
}