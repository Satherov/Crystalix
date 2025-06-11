package com.satherov.crystalix.client;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import com.satherov.crystalix.content.item.CrystalixWand;
import com.satherov.crystalix.content.properties.BlockProperties;
import com.satherov.crystalix.core.CrystalixUtil;
import com.satherov.crystalix.core.lang.CrystalixLanguage;
import com.satherov.crystalix.core.lang.ILangEntry;
import com.satherov.crystalix.network.CrystalixNetworking;
import com.satherov.crystalix.network.CyclePropertyPayload;

import org.lwjgl.glfw.GLFW;

public class KeybindManager {

    public static final KeyMapping CYCLE_INVISIBLE = register(CrystalixLanguage.KEY_INVISIBLE, GLFW.GLFW_KEY_B);
    public static final KeyMapping CYCLE_SHADELESS = register(CrystalixLanguage.KEY_SHADELESS, GLFW.GLFW_KEY_Z);
    public static final KeyMapping CYCLE_REINFORCED = register(CrystalixLanguage.KEY_REINFORCED, GLFW.GLFW_KEY_X);
    public static final KeyMapping CYCLE_LIGHT = register(CrystalixLanguage.KEY_LIGHT, GLFW.GLFW_KEY_C);
    public static final KeyMapping CYCLE_GHOST = register(CrystalixLanguage.KEY_GHOST, GLFW.GLFW_KEY_V);

    private static KeyMapping register(ILangEntry entry, int key) {
        return new KeyMapping(entry.getTranslationKey(), key, CrystalixLanguage.KEY_CATEGORY.getTranslationKey());
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.Key event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack wand = CrystalixUtil.getWand(player);
        if (wand.isEmpty() || !(wand.getItem() instanceof CrystalixWand)) return;

        if (CYCLE_INVISIBLE.consumeClick()) {
            BlockProperties properties = new BlockProperties(wand);
            properties.invisible.next();
            CrystalixNetworking.sendToServer(new CyclePropertyPayload(properties.invisible));
        }
        if (CYCLE_SHADELESS.consumeClick()) {
            BlockProperties properties = new BlockProperties(wand);
            properties.shadeless.next();
            CrystalixNetworking.sendToServer(new CyclePropertyPayload(properties.shadeless));
        }
        if (CYCLE_REINFORCED.consumeClick()) {
            BlockProperties properties = new BlockProperties(wand);
            properties.reinforced.next();
            CrystalixNetworking.sendToServer(new CyclePropertyPayload(properties.reinforced));
        }
        if (CYCLE_LIGHT.consumeClick()) {
            BlockProperties properties = new BlockProperties(wand);
            properties.light.next();
            CrystalixNetworking.sendToServer(new CyclePropertyPayload(properties.light));
        }
        if (CYCLE_GHOST.consumeClick()) {
            BlockProperties properties = new BlockProperties(wand);
            properties.ghost.next();
            CrystalixNetworking.sendToServer(new CyclePropertyPayload(properties.ghost));
        }
    }
}
