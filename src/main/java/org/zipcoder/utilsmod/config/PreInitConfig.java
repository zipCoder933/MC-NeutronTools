package org.zipcoder.utilsmod.config;

import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.nio.file.Path;

import static org.zipcoder.utilsmod.NeutronTools.LOGGER;

public class PreInitConfig {
    public PreInitConfig() {
        try {

            Path path = FMLPaths.CONFIGDIR.get();
            File configFile = new File(path.toFile(), "mp-utils-config.toml");
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
//    public class ExposedOPCommand{
//        public String command;
//        public String keyword;
//        public boolean allowArguments = false;
//    }

    public boolean crashCommands = false;
//    public ExposedOPCommand[] exposeOPCommands = new ExposedOPCommand[0];

    /**
     * Write a new config
     */
    private void writeConfig(FileConfig config) {
        config.set("common.crash_commands", crashCommands);

//        // Save exposeOPCommands array
//        try {
//            List<Map<String, Object>> list = new ArrayList<>();
//            for (ExposedOPCommand cmd : exposeOPCommands) {
//                Map<String, Object> map = new HashMap<>();
//                map.put("command", cmd.command);
//                map.put("keyword", cmd.keyword);
//                map.put("allow_arguments", cmd.allowArguments);
//                list.add(map);
//            }
//            config.set("common.expose_op_commands", list);
//        }catch (Exception e) {
//            LOGGER.error("An error occurred writing exposed op commands to pre-init config!", e);
//        }

        config.save();
    }

    /**
     * Load the config
     * NOTE that doubles in the config MUST have .0 at the end otherwise it will be read as an int
     */
    private void loadConfig(FileConfig config) {
        config.load();

//        // Load exposeOPCommands array
//        try {
//            List<?> list = config.get("common.expose_op_commands");
//            if (list != null) {
//                exposeOPCommands = new ExposedOPCommand[list.size()];
//                for (int i = 0; i < list.size(); i++) {
//                    Object obj = list.get(i);
//                    if (obj instanceof Config) {
//                        Config cfg = (Config) obj;
//                        ExposedOPCommand cmd = new ExposedOPCommand();
//                        cmd.command = cfg.getOrElse("command", "");
//                        cmd.keyword = cfg.getOrElse("keyword", "");
//                        cmd.allowArguments = cfg.getOrElse("allow_arguments", false);
//                        LOGGER.info("EXPOSED OP COMMAND: \"" + cmd.command + "\"; Keyword: \"" + cmd.keyword+"\"; Allow arguments: "+cmd.allowArguments);
//                        exposeOPCommands[i] = cmd;
//                    }
//                }
//            } else {
//                exposeOPCommands = new ExposedOPCommand[0];
//            }
//        } catch (Exception e) {
//            LOGGER.error("An error occurred loading exposed op commands from pre-init config!", e);
//        }

        crashCommands = config.getOrElse("common.crash_commands", false);
    }
}
