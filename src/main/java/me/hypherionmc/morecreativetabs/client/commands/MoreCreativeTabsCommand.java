package me.hypherionmc.morecreativetabs.client.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import me.hypherionmc.morecreativetabs.ModConstants;
import me.hypherionmc.morecreativetabs.MoreCreativeTabs;
import me.hypherionmc.morecreativetabs.client.tabs.CustomCreativeTabRegistry;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @author HypherionSA
 * Register Client Side Commands
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MoreCreativeTabsCommand {

    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("mct")
                .requires(source -> source.hasPermission(2)) // Only players with permission level 2 or higher see this command
                .then(Commands.literal("showTabNames")
                        .then(Commands.argument("enabled", BoolArgumentType.bool()).executes(context -> {
                            boolean enabled = BoolArgumentType.getBool(context, "enabled");
                            CustomCreativeTabRegistry.INSTANCE.setShowTabNames(enabled);
                            context.getSource().sendSuccess(() -> enabled ? Component.literal("Showing tab registry names") : Component.literal("Showing tab names"), true);
                            return 1;
                        }))).then(Commands.literal("reloadTabs").executes(context -> {
                    MoreCreativeTabs.reloadResources();
                    context.getSource().sendSuccess(() -> Component.literal("Reloaded Custom Tabs"), true);
                    return 1;
                }))
        );
    }

}
