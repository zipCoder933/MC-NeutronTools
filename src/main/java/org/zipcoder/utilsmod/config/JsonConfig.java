package org.zipcoder.utilsmod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonConfig {
    final JsonFile file;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public JsonConfig() {
        // Write to file
        Path filePath = Paths.get("config", "modpack_utils.json");

        if (filePath.toFile().exists()) {// Read from file

            try (FileReader reader = new FileReader(filePath.toFile())) {
                file = GSON.fromJson(reader, JsonFile.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {//Write to file

            file = new JsonFile();
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                GSON.toJson(file, writer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public boolean shouldRemoveItemFromTab(String tab, Item item) {
        TabEntry entry = null;//Find the tab
        for (int i = 0; i < file.removeItems.length; i++) {
            if (file.removeItems[i].creativeTab.equals(tab)) {
                entry = file.removeItems[i];
            }
        }

        //Find the item
        if (entry != null) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            String itemID = id.toString();
            for (int i = 0; i < entry.items.length; i++) {
                if (entry.items[i].equals(itemID)) return true;
            }
        }

        return false;
    }

    public boolean shouldRemoveCreativeTab(String name) {
        for (int i = 0; i < file.removeCreativeTabs.length; i++) {
            if (file.removeCreativeTabs[i].equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static class JsonFile {
        public String[] removeCreativeTabs = {};

        public TabEntry[] removeItems = {
                new TabEntry("creativetab.course_tab", new String[]{"minecraft:acacia_boat"})
        };

        public TabEntry[] addItems = {
                new TabEntry("creativetab.course_tab", new String[]{"test"})
        };
    }

    public static class TabEntry {
        public TabEntry(String tab, String[] items) {
            this.creativeTab = tab;
            this.items = items;
        }

        public String creativeTab;
        public String[] items;
    }

}
