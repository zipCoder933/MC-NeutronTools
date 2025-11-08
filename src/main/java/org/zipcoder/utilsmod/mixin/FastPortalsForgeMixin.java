package org.zipcoder.utilsmod.mixin;

import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zipcoder.utilsmod.NeutronTools;

import java.time.LocalDate;

@Mixin(Player.class)
public class FastPortalsForgeMixin {
    @Shadow
    @Final
    private Abilities abilities;

    @Inject(method = "getPortalWaitTime", at = @At("HEAD"), cancellable = true)
    public void getPortalWaitTime(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(abilities.invulnerable ? 1 : NeutronTools.CONFIG.portalWaitTime);
    }
}