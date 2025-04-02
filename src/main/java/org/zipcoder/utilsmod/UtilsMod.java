package org.zipcoder.utilsmod;

import com.mojang.logging.LogUtils;
import me.hypherionmc.morecreativetabs.MoreCreativeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.zipcoder.utilsmod.config.PreInitConfig;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(UtilsMod.MODID)
public class UtilsMod {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "utilsmod";
    // Directly reference a slf4j logger
    //The logger is a central point for logging
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final PreInitConfig CONFIG = new PreInitConfig();

    /**
     * Custom creative tab test
     */
//    public static final DeferredRegister<CreativeModeTab> TEST_CREATIVE_TABS =
//            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

//    public static final RegistryObject<CreativeModeTab> COURSE_TAB = CREATIVE_MODE_TABS.register("course_tab",
//            () -> CreativeModeTab.builder().icon(() -> new ItemStack(
//                            Items.ACACIA_BOAT
//                    ))
//                    .title(Component.translatable("utilsmod.course_tab"))
//                    .displayItems((displayParameters, output) -> {
//                        output.accept(Items.ACACIA_BOAT);
//                        output.accept(Items.SPRUCE_BOAT);
//                        output.accept(Items.OAK_BOAT);
//                        output.accept(Items.BIRCH_BOAT);
//                    }).build());
    public UtilsMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        //Register creative tabs
//        TEST_CREATIVE_TABS.register(modEventBus);

        //Setup MCT mod
        MoreCreativeTabs mct = new MoreCreativeTabs();
    }


    private void commonSetup(final FMLCommonSetupEvent event) {
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
}
