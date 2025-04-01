package org.zipcoder.utilsmod.mixin.moreCreativeTabs;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.CreativeModeTabRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(CreativeModeTabRegistry.class)
public abstract class ForgeCreativeTabRegistryMixin {

    @Shadow
    public static List<CreativeModeTab> getDefaultTabs() {
        return null;
    }

//    @Inject(method = "getSortedCreativeModeTabs", at = @At("RETURN"), cancellable = true)
//    private static void injectCustomTabs(CallbackInfoReturnable<List<CreativeModeTab>> cir) {
//        cir.setReturnValue(CustomCreativeTabRegistry.INSTANCE.sortedTabs().stream().filter(t -> !getDefaultTabs().contains(t)).toList());
//    }

}
