package com.satherov.crystalix.client;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.CrystalixUtil;
import com.satherov.crystalix.content.item.CrystalixWand;
import com.satherov.crystalix.content.properties.BlockProperties;
import com.satherov.crystalix.network.CrystalixNetworking;
import com.satherov.crystalix.network.CyclePropertyPayload;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.lwjgl.glfw.GLFW;

public class KeybindManager {

    public static final KeyMapping CYCLE_SHADELESS = new KeyMapping(key("cycle_shadeless"), GLFW.GLFW_KEY_Z, category());
    public static final KeyMapping CYCLE_REINFORCED = new KeyMapping(key("cycle_reinforced"), GLFW.GLFW_KEY_X, category());
    public static final KeyMapping CYCLE_LIGHT = new KeyMapping(key("cycle_light"), GLFW.GLFW_KEY_C, category());
    public static final KeyMapping CYCLE_GHOST = new KeyMapping(key("cycle_ghost"), GLFW.GLFW_KEY_V, category());

    private static String category() {
        return key("category");
    }

    private static String key(String name) {
        return String.join(".", "key", Crystalix.MOD_ID, name);
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.Key event) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        ItemStack wand = CrystalixUtil.getWand(player);
        if (wand.isEmpty() || !(wand.getItem() instanceof CrystalixWand)) return;

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
