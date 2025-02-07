package com.satherov.crystalix.client;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.item.CrystalixWand;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
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

        ItemStack wand = wand(player);
        if (wand.isEmpty() || !(wand.getItem() instanceof CrystalixWand crystalixWand)) return;

        if (CYCLE_SHADELESS.consumeClick()) crystalixWand.cycleShadeless(player);
        if (CYCLE_REINFORCED.consumeClick()) crystalixWand.cycleReinforced(player);
        if (CYCLE_LIGHT.consumeClick()) crystalixWand.cycleLight(player);
        if (CYCLE_GHOST.consumeClick()) crystalixWand.cycleGhost(player);
    }

    public static ItemStack wand(Player player) {
        ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);

        if (!mainHand.isEmpty() && mainHand.getItem() instanceof CrystalixWand) {
            return mainHand;
        } else if (!offHand.isEmpty() && offHand.getItem() instanceof CrystalixWand) {
            return offHand;
        }
        return ItemStack.EMPTY;
    }
}
