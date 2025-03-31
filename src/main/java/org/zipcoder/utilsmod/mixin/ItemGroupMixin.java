package org.zipcoder.utilsmod.mixin;

import java.util.Collection;
import java.util.Set;

import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import org.zipcoder.utilsmod.UtilsMod;

@Mixin(value = CreativeModeTab.class, priority = 10000)
public abstract class ItemGroupMixin {
    @Shadow
    private Collection<ItemStack> displayItems = ItemStackLinkedSet.createTypeAndTagSet();
    @Shadow
    private Set<ItemStack> displayItemsSearchTab = ItemStackLinkedSet.createTypeAndTagSet();

    @Shadow
    @Final
    private Component displayName;

    @Inject(method = "buildContents", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CreativeModeTab;rebuildSearchTree()V"))
    private void item_obliterator_buildContents(CreativeModeTab.ItemDisplayParameters displayContext, CallbackInfo ci) {
        String tabName = displayName.getString();
        System.out.println("CREATIVE MODE TAB: " + tabName);
        boolean removeTab = UtilsMod.CONFIG.shouldRemoveCreativeTab(tabName);

        displayItems.removeIf(stack -> {
            return removeTab || UtilsMod.CONFIG.shouldRemoveItemFromTab(tabName, stack.getItem());
        });

        displayItemsSearchTab.removeIf(stack -> {
            return removeTab || UtilsMod.CONFIG.shouldRemoveItemFromTab(tabName, stack.getItem());
        });
    }
}