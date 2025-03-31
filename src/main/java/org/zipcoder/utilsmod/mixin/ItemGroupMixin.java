package org.zipcoder.utilsmod.mixin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.zipcoder.utilsmod.UtilsMod;
import org.zipcoder.utilsmod.config.JsonConfig;

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
        CreativeModeTab thisTab = (CreativeModeTab) (Object) this;
        ResourceLocation key = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(thisTab);
        assert key != null;
        String tabID = key.toString();

        System.out.println("CREATIVE MODE TAB: " + tabID);


        /**
         * Item removal
         */
        boolean removeTab = UtilsMod.CONFIG.shouldRemoveCreativeTab(tabID);
        JsonConfig.TabEntry removalEntry = UtilsMod.CONFIG.getRemoveTabEntry(tabID);
        displayItems.removeIf(stack -> {
            return removeTab || UtilsMod.CONFIG.shouldRemoveItemFromTab(removalEntry, stack.getItem());
        });

        displayItemsSearchTab.removeIf(stack -> {
            return removeTab || UtilsMod.CONFIG.shouldRemoveItemFromTab(removalEntry, stack.getItem());
        });


        /**
         * Item addition
         * We wont add any items to a tab if we already specified to remove it
         */
        JsonConfig.TabEntry addEntry = UtilsMod.CONFIG.getAddTabEntry(tabID);
        if (!removeTab && addEntry != null) {

            HashSet<ItemStack> unique = new HashSet<>(displayItems);
            for (String itemID : addEntry.items) {
                if (!contains(unique, itemID)) {//If the item doesnt exist
                    Item item = getItemByID(itemID); //Add it
                    if (item != null) displayItems.add(new ItemStack(item));
                }
            }

            HashSet<ItemStack> uniqueSearch = new HashSet<>(displayItemsSearchTab);
            for (String itemID : addEntry.items) {
                if (!contains(uniqueSearch, itemID)) {//If the item doesnt exist
                    Item item = getItemByID(itemID); //Add it
                    if (item != null) displayItems.add(new ItemStack(item));
                }
            }

        }
    }

    private Item getItemByID(String itemID) {
        String[] splitID = itemID.split(":");
        if (splitID.length < 2) return null;

        ResourceLocation resourceLoc = new ResourceLocation(splitID[0], splitID[1]);
        return BuiltInRegistries.ITEM.get(resourceLoc);
    }

    private boolean contains(HashSet<ItemStack> unique, String itemID) {
        for (ItemStack i : unique) {
            //Get the ID
            String id = BuiltInRegistries.ITEM.getKey(i.getItem()).toString();

            //If unique contains itemID, return true
            if (id.equals(itemID)) return true;
        }
        return false;
    }
}