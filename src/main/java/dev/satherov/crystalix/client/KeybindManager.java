package dev.satherov.crystalix.client;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.client.lang.CSLanguage;
import dev.satherov.crystalix.client.renderer.RadialMenuScreen;
import dev.satherov.crystalix.common.block.CrystalixGlass;
import dev.satherov.crystalix.common.item.CrystalixWand;
import dev.satherov.crystalix.common.properties.CSProperties;
import dev.satherov.crystalix.common.properties.IProperty;
import dev.satherov.crystalix.core.CSRegistry;
import dev.satherov.crystalix.core.network.CSNetwork;
import dev.satherov.crystalix.core.network.CyclePropertyPayload;
import dev.satherov.crystalix.core.network.SwapPropertiesPayload;
import dev.satherov.crystalix.core.network.ToggleColorless;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = Crystalix.MOD_ID, value = Dist.CLIENT)
public class KeybindManager {
    
    public static final KeyMapping SCREEN_OPENER = KeybindManager.register(CSLanguage.KEY_WAND_CONFIG, GLFW.GLFW_KEY_V);
    public static final KeyMapping COPY_PROPERTIES = KeybindManager.register(CSLanguage.KEY_COPY_PROPERTIES, GLFW.GLFW_KEY_X);
    public static final KeyMapping APPLY_COLORLESS = KeybindManager.register(CSLanguage.KEY_APPLY_COLORLESS, GLFW.GLFW_KEY_LEFT_CONTROL);
    
    private static KeyMapping register(CSLanguage entry, int key) {
        return new KeyMapping(entry.key(), key, CSLanguage.KEY_CATEGORY.key());
    }
    
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.screen instanceof ChatScreen) return;
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
        
        if (KeybindManager.COPY_PROPERTIES.matches(event.getKey(), event.getScanCode())) {
            if (mc.hitResult == null || mc.hitResult.getType() != HitResult.Type.BLOCK) return;
            BlockHitResult hit = (BlockHitResult) mc.hitResult;
            BlockPos pos = hit.getBlockPos();
            BlockState state = mc.level.getBlockState(pos);
            if (!(state.getBlock() instanceof CrystalixGlass)) return;
            CSNetwork.sendToServer(new SwapPropertiesPayload(pos));
        }
        
        if (KeybindManager.APPLY_COLORLESS.consumeClick()) {
            CSNetwork.sendToServer(new ToggleColorless(Boolean.FALSE.equals(wand.get(CSRegistry.APPLY_COLORLESS))));
        }
    }
}
