package org.zipcoder.utilsmod.mixin.moreCreativeTabs;

import me.hypherionmc.morecreativetabs.client.data.CustomCreativeTabJsonHelper;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import me.hypherionmc.morecreativetabs.utils.CreativeTabUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static me.hypherionmc.morecreativetabs.utils.CreativeTabUtils.getTabKey;

@Mixin(CreativeModeTab.class)
public abstract class CreativeModeTabMixin {

    @Shadow
    private Collection<ItemStack> displayItems;
    @Shadow
    private Set<ItemStack> displayItemsSearchTab;

    @Shadow
    public abstract void rebuildSearchTree();

    @Shadow
    @Final
    private Component displayName;

    @Shadow
    public abstract Collection<ItemStack> getDisplayItems();

    @Inject(method = "buildContents", at = @At("HEAD"), cancellable = true)
    private void injectBuildContents(CreativeModeTab.ItemDisplayParameters arg, CallbackInfo ci) {
        CreativeModeTab self = (CreativeModeTab) (Object) this;

        if (CustomCreativeTabRegistry.INSTANCE.getCustomTabs().contains(self) && CustomCreativeTabRegistry.INSTANCE.getTabItems().containsKey(self)) {
            ci.cancel();

            displayItems.clear();
            displayItemsSearchTab.clear();
            List<ItemStack> stacks = CustomCreativeTabRegistry.INSTANCE.getTabItems().get(self);

            displayItems.addAll(stacks);
            displayItemsSearchTab.addAll(stacks);
            rebuildSearchTree();
        }
    }

    @Inject(method = "hasAnyItems", at = @At("RETURN"), cancellable = true)
    private void injectHasAnyItems(CallbackInfoReturnable<Boolean> cir) {
        CreativeModeTab self = (CreativeModeTab) ((Object) this);

        if (CustomCreativeTabRegistry.INSTANCE.getCustomTabs().contains(self) && CustomCreativeTabRegistry.INSTANCE.getTabItems().containsKey(self)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void injectDisplayName(CallbackInfoReturnable<Component> cir) {
        Component value = this.displayName;

        CreativeTabUtils.replacementTab(convertName(getTabKey(value))).ifPresent(tabData -> {
            if (!CustomCreativeTabRegistry.INSTANCE.isShowTabNames()) {
                cir.setReturnValue(Component.translatable(CreativeTabUtils.prefix(tabData.getLeft().getTabName())));
            }
        });

        if (!CustomCreativeTabRegistry.INSTANCE.isShowTabNames())
            return;

        cir.setReturnValue(Component.literal(getTabKey(value)));
    }

    @Inject(method = "contains", at = @At("RETURN"), cancellable = true)
    private void injectContains(ItemStack arg, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(getDisplayItems().contains(arg));
    }

    @Inject(method = "getDisplayItems", at = @At("RETURN"), cancellable = true)
    private void injectDisplayItemsFilter(CallbackInfoReturnable<Collection<ItemStack>> cir) {
        cir.setReturnValue(filterItems(cir.getReturnValue()));
    }

    @Inject(method = "getSearchTabDisplayItems", at = @At("RETURN"), cancellable = true)
    private void injectSearchItemsFilter(CallbackInfoReturnable<Collection<ItemStack>> cir) {
        cir.setReturnValue(filterItems(cir.getReturnValue()));
    }

    @Unique
    private ItemStack customIconItem = null;

    @Unique
    private boolean checkedForCustomIconItem = false;

    //TODO: Make sure things like this arent happening anywhere else
    //This method is called EVERY time the icon is requested, so we need to cache it
    @Inject(method = "getIconItem", at = @At("RETURN"), cancellable = true)
    private void injectIcon(CallbackInfoReturnable<ItemStack> cir) {
        if (!checkedForCustomIconItem) {//We only want to do this once
            CreativeTabUtils.replacementTab(convertName(getTabKey(this.displayName))).ifPresent(tabData -> {
                customIconItem = CreativeTabUtils.makeTabIcon(tabData.getLeft()).get();
            });
            checkedForCustomIconItem = true;
        }
        if (customIconItem != null && !customIconItem.isEmpty())
            cir.setReturnValue(customIconItem);
    }

    @Unique
    private Collection<ItemStack> filterItems(Collection<ItemStack> inputStacks) {
        CreativeModeTab self = (CreativeModeTab) ((Object) this);
        if (CustomCreativeTabRegistry.INSTANCE.getCustomTabs().contains(self) || self.getType() == CreativeModeTab.Type.SEARCH)
            return inputStacks;

        Collection<ItemStack> oldStacks = this.displayItems;

        Optional<Pair<CustomCreativeTabJsonHelper, List<ItemStack>>> replacementTab = CreativeTabUtils.replacementTab(convertName(getTabKey(this.displayName)));
        if (replacementTab.isPresent()) {
            List<ItemStack> returnStacks = new ArrayList<>(replacementTab.get().getRight());

            if (replacementTab.get().getLeft().getTabItems().stream().anyMatch(i -> i.getName().equalsIgnoreCase("existing"))) {
                returnStacks.addAll(oldStacks.stream().filter(i -> !CustomCreativeTabRegistry.INSTANCE.getHiddenItems().contains(i.getItem())).toList());
            }

            return returnStacks;
        }

        Collection<ItemStack> filteredStacks = new ArrayList<>();

        if (oldStacks != null && !oldStacks.isEmpty()) {
            oldStacks.forEach(i -> {
                if (!CustomCreativeTabRegistry.INSTANCE.getHiddenItems().contains(i.getItem())) {
                    filteredStacks.add(i);
                }
            });

            if (!filteredStacks.isEmpty()) {
                return filteredStacks;
            }
        }

        return inputStacks;
    }

    @Unique
    private String convertName(String tabName) {
        return tabName.replace(".", "_");
    }
}
