//package org.zipcoder.utilsmod.mixin;
//
//
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.CreativeModeTab;
//import net.minecraftforge.registries.DeferredRegister;
//import net.minecraftforge.registries.RegistryObject;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//import java.util.function.Supplier;
//
//@Mixin(DeferredRegister.class)
//public class MixinDeferredRegister {
//
//    @Inject(method = "register(Ljava/lang/String;Ljava/util/function/Supplier;)Lnet/minecraftforge/registries/RegistryObject;", at = @At("HEAD"), cancellable = true)
//    private <I extends CreativeModeTab> void injectRegisterCreativeModeTab(String name, Supplier<? extends I> sup, CallbackInfoReturnable<RegistryObject<I>> cir) {
//        System.out.println("TAB NAME: " + name);
//        if (name.equals("course_tab")) {
//            cir.cancel(); // Cancel the original method's execution.
//            cir.setReturnValue(null); // Return null, or a default value, as appropriate.
//        }
//    }
//}