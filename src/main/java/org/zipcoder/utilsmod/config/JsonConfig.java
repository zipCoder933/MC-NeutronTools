package org.zipcoder.utilsmod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.zipcoder.utilsmod.UtilsMod;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class JsonConfig {
    public final JsonFile file;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public JsonConfig() {
        // Write to file
        Path filePath = Paths.get("config", "modpack_utils.json");

        if (filePath.toFile().exists()) {// Read from file

            try (FileReader reader = new FileReader(filePath.toFile())) {
                file = GSON.fromJson(reader, JsonFile.class);
            } catch (IOException e) {
                UtilsMod.LOGGER.error("Couldn't load config file");
            }

        } else {//Write to file

            file = new JsonFile();
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                GSON.toJson(file, writer);
            } catch (IOException e) {
                UtilsMod.LOGGER.error("Couldn't save new config file");
            }

        }
    }

    public TabEntry getRemoveTabEntry(String tab) {
        TabEntry entry = null;//Find the tab
        for (int i = 0; i < file.removeItems.length; i++) {
            if (file.removeItems[i].creativeTab.equals(tab)) {
                entry = file.removeItems[i];
            }
        }
        return entry;
    }

    public TabEntry getAddTabEntry(String tab) {
        TabEntry entry = null;//Find the tab
        for (int i = 0; i < file.addItems.length; i++) {
            if (file.addItems[i].creativeTab.equals(tab)) {
                entry = file.addItems[i];
            }
        }
        return entry;
    }

    public boolean shouldRemoveItemFromTab(TabEntry entry, Item item) {
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
                new TabEntry("test.course_tab", new String[]{"myMod:testItem"})
        };

        public TabEntry[] addItems = {
                new TabEntry("test.course_tab", new String[]{"myMod:testItem"})
        };

        public boolean crashCommands = false;
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
