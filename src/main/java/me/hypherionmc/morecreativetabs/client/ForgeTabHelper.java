package me.hypherionmc.morecreativetabs.client;

/**
 * @author HypherionSA
 * UNUSED ON FORGE
 */
/*public class ForgeTabHelper implements ITabHelper {

    @Override
    public void updateCreativeTabs(List<CreativeModeTab> tabs) {
        //Minecraft.getInstance().createSearchTrees();
        Set<ItemStack> set = ItemStackLinkedSet.createTypeAndTagSet();
        CreativeModeTab searchTab = CreativeModeTabs.searchTab();
        CreativeModeTabAccessor accessor = (CreativeModeTabAccessor) searchTab;
        for (CreativeModeTab t : CustomCreativeTabRegistry.current_tabs) {
            if (t.getType() != CreativeModeTab.Type.SEARCH) {
                set.addAll(t.getSearchTabDisplayItems());
            }
        }
        accessor.getDisplayItemSearchTab().clear();
        accessor.getDisplayItemsVariable().clear();
        accessor.getDisplayItemsVariable().addAll(set);
        accessor.getDisplayItemSearchTab().addAll(set);

        searchTab.rebuildSearchTree();
    }
}*/
