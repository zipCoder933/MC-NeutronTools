package org.zipcoder.utilsmod.config;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.nio.file.Path;

import static org.zipcoder.utilsmod.UtilsMod.LOGGER;

public class PreInitConfig {
    public PreInitConfig() {
        try {

            Path path = FMLPaths.CONFIGDIR.get();
            File configFile = new File(path.toFile(), "cyclic-pre-init.toml");
            try (FileConfig config = FileConfig.builder(configFile, TomlFormat.instance()).build()) {
                if (configFile.exists()) {
                    loadConfig(config);
                } else {
                    writeConfig(config);
                }
            }

        } catch (Exception e) {
            LOGGER.error("An error occurred initializing pre-init config!", e);
        }
    }

    /**
     * Default values go here
     * Only boolean, int, double and string are supported
     * NO FLOATS ALLOWED!
     */
    public boolean crashCommands = false;

    /**
     * Write a new config
     */
    private void writeConfig(FileConfig config) {
        config.set("common.crash_commands", crashCommands);
        config.save();
    }

    /**
     * Load the config
     * NOTE that doubles in the config MUST have .0 at the end otherwise it will be read as an int
     */
    private void loadConfig(FileConfig config) {
        config.load();
        crashCommands = config.getOrElse("common.crash_commands", false);
    }
}
