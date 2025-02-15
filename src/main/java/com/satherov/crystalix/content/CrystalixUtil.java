package com.satherov.crystalix.content;

import com.satherov.crystalix.content.item.CrystalixWand;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CrystalixUtil {

    public static ItemStack getWand(Player player) {
        ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);

        if (!mainHand.isEmpty() && mainHand.getItem() instanceof CrystalixWand) {
            return mainHand;
        } else if (!offHand.isEmpty() && offHand.getItem() instanceof CrystalixWand) {
            return offHand;
        } else {
            return ItemStack.EMPTY;
        }
    }
}
