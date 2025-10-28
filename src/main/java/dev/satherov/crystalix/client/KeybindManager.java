package dev.satherov.crystalix.client;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.client.lang.CSLanguage;
import dev.satherov.crystalix.client.renderer.RadialMenuScreen;
import dev.satherov.crystalix.common.item.CrystalixWand;
import dev.satherov.crystalix.common.properties.CSProperties;
import dev.satherov.crystalix.common.properties.IProperty;
import dev.satherov.crystalix.core.network.CSNetwork;
import dev.satherov.crystalix.core.network.CyclePropertyPayload;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = Crystalix.MOD_ID, value = Dist.CLIENT)
public class KeybindManager {
    
    public static final KeyMapping SCREEN_OPENER = register(CSLanguage.KEY_WAND_CONFIG, GLFW.GLFW_KEY_V);
    
    private static KeyMapping register(CSLanguage entry, int key) {
        return new KeyMapping(entry.key(), key, CSLanguage.KEY_CATEGORY.key());
    }
    
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        ItemStack wand = CrystalixWand.find(mc.player);
        if (wand.isEmpty()) return;
        
        if (KeybindManager.SCREEN_OPENER.matches(event.getKey(), event.getScanCode())) {
            if (event.getAction() == GLFW.GLFW_PRESS) {
                RadialMenuScreen menu = new RadialMenuScreen();
                CSProperties properties = CSProperties.of(wand);
                
                for (IProperty<?> property : properties.properties().values()) {
                    menu.addMenuItem(property, dir -> {
                                         CSNetwork.sendToServer(new CyclePropertyPayload(property.location(), dir));
                                     }
                    );
                }
                
                mc.setScreen(menu);
            }
        }
    }
}
