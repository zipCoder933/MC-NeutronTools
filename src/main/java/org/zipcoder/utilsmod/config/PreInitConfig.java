//package org.zipcoder.utilsmod.config;
//
//import com.electronwill.nightconfig.core.file.FileConfig;
//import com.electronwill.nightconfig.toml.TomlFormat;
//import net.minecraftforge.fml.loading.FMLPaths;
//import org.zipcoder.cyclic.Cyclic;
//
//import java.io.File;
//import java.nio.file.Path;
//
//public class PreInitConfig {
//    public PreInitConfig() {
//        try {
//
//            Path path = FMLPaths.CONFIGDIR.get();
//            File configFile = new File(path.toFile(), "cyclic-pre-init.toml");
//            try (FileConfig config = FileConfig.builder(configFile, TomlFormat.instance()).build()) {
//                if (configFile.exists()) {
//                    loadConfig(config);
//                } else {
//                    writeConfig(config);
//                }
//            }
//
//        } catch (Exception e) {
//            Cyclic.LOGGER.error("An error occurred initializing pre-init config!", e);
//        }
//    }
//
//    /**
//     * Default values go here
//     * Only boolean, int, double and string are supported
//     * NO FLOATS ALLOWED!
//     */
//    public boolean client_nightVisionKey = true;
//
//    public int potion_freeze1Duration = 15;
//    public int potion_freeze2Duration = 50;
//
//    public boolean attackRange = true;
//    public double attackRange_reach = 1.25f;
//
//    public boolean reachDistance = true;
//    public double reachDistance_reach = 1.5f;
//
//    public double leatherShieldDurabilityMultiplier = 1.5F;
//    public double boneShieldDurabilityMultiplier = 2F;
//    public double obsidianShieldDurabilityMultiplier = 5F;
//
//    /**
//     * Write a new config
//     */
//    private void writeConfig(FileConfig config) {
//        //Client
//        config.set("client.night_vision_key", client_nightVisionKey);
//        //Common
//        config.set("common.potion.freeze_1_duration", potion_freeze1Duration);
//        config.set("common.potion.freeze_2_duration", potion_freeze2Duration);
//
//        config.set("common.attackRange.attack_range_potion_enabled", attackRange);
//        config.set("common.attackRange.attack_range_multiplier", attackRange_reach);
//
//        config.set("common.reachDistance.reach_distance_potion_enabled", reachDistance);
//        config.set("common.reachDistance.reach_distance_multiplier", reachDistance_reach);
//
//        config.set("common.shield.leather_durability_multiplier", leatherShieldDurabilityMultiplier);
//        config.set("common.shield.bone_durability_multiplier", boneShieldDurabilityMultiplier);
//        config.set("common.shield.obsidian_durability_multiplier", obsidianShieldDurabilityMultiplier);
//        config.save();
//    }
//
//    /**
//     * Load the config
//     * NOTE that doubles in the config MUST have .0 at the end otherwise it will be read as an int
//     */
//    private void loadConfig(FileConfig config) {
//        config.load();
//        client_nightVisionKey = config.getOrElse("client.night_vision_key", true);
//
//        potion_freeze1Duration = config.getOrElse("common.potion.freeze_1_duration", potion_freeze1Duration);
//        potion_freeze2Duration = config.getOrElse("common.potion.freeze_2_duration", potion_freeze2Duration);
//
//        attackRange = config.getOrElse("common.attackRange.attack_range_potion_enabled", attackRange);
//        attackRange_reach = config.getOrElse("common.attackRange.attack_range_multiplier", attackRange_reach);
//
//        reachDistance = config.getOrElse("common.reachDistance.reach_distance_potion_enabled", reachDistance);
//        reachDistance_reach = config.getOrElse("common.reachDistance.reach_distance_multiplier", reachDistance_reach);
//
//        leatherShieldDurabilityMultiplier = config.getOrElse("common.shield.leather_durability_multiplier", leatherShieldDurabilityMultiplier);
//        boneShieldDurabilityMultiplier = config.getOrElse("common.shield.bone_durability_multiplier", boneShieldDurabilityMultiplier);
//        obsidianShieldDurabilityMultiplier = config.getOrElse("common.shield.obsidian_durability_multiplier", obsidianShieldDurabilityMultiplier);
//    }
//}
