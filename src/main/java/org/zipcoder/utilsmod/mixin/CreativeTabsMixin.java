//package org.zipcoder.utilsmod.mixin;
//
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.ItemStackLinkedSet;
//import org.spongepowered.asm.mixin.Mixin;
//import net.minecraft.world.item.CreativeModeTab;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//import org.zipcoder.utilsmod.mixinInterface.CreativeModeTab_I;
//
//import java.util.Collection;
//
//@Mixin(CreativeModeTab.class)
//public class CreativeTabsMixin implements CreativeModeTab_I {
//    @Shadow
//    private Collection<ItemStack> displayItems;
//
//    /**
//     * If there are no items in the creative tab, the tab doesnt show up
//     */
//    public void clearItems() {
//        displayItems.clear();
//    }
//
////    @Inject(method = "shouldDisplay", at = @At("HEAD"), cancellable = true)
////    private void injectShouldDisplay(CallbackInfoReturnable<Boolean> cir) {
////        cir.setReturnValue(false);
////    }
//}