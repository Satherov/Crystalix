package com.satherov.crystalix.content.item;

import java.util.*;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.CrystalixConfig;
import com.satherov.crystalix.content.block.CrystalixBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class CrystalixWand extends Item {

    private boolean shadelessMode = false;
    private boolean reinforcedMode = false;
    private CrystalixBlock.Light lightMode  = CrystalixBlock.Light.NONE;
    private CrystalixBlock.Ghost ghostMode  = CrystalixBlock.Ghost.BLOCK_ALL;

    public CrystalixWand(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(getTranslation("shadeless", shadelessMode));
        tooltipComponents.add(getTranslation("reinforced", reinforcedMode));
        tooltipComponents.add(getTranslation("light", lightMode.name().toLowerCase(Locale.ROOT)));
        tooltipComponents.add(getTranslation("ghost", ghostMode.name().toLowerCase(Locale.ROOT)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!player.isCrouching()) return InteractionResultHolder.pass(stack);

        BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = hit.getBlockPos();
            if (applyToBlock(level, pos, player)) {
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }
        }

        return InteractionResultHolder.pass(stack);
    }


    public boolean applyToBlock(Level level, BlockPos pos, Player player) {
        if (!(level.getBlockState(pos).getBlock() instanceof CrystalixBlock)) {
            return false;
        }

        System.out.println(player.isCrouching());
        Set<BlockPos> blocksToModify = player.isCrouching()
                ? getConnectedBlocks(level, pos, level.getBlockState(pos).getBlock(), CrystalixConfig.max_wand_edit)
                : Set.of(pos);

        for (BlockPos targetPos : blocksToModify) {
            System.out.println("targetPos: " + targetPos);
            BlockState newState = level.getBlockState(targetPos)
                    .setValue(CrystalixBlock.SHADED, !this.isShadeless())
                    .setValue(CrystalixBlock.REINFORCED, this.isReinforced())
                    .setValue(CrystalixBlock.LIGHT, this.getLightMode())
                    .setValue(CrystalixBlock.GHOST, this.getGhostMode());

            level.setBlock(targetPos, newState, 3);
        }

        return true;
    }



    public Set<BlockPos> getConnectedBlocks(Level level, BlockPos start, Block targetBlock, int maxBlocks) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty() && visited.size() < maxBlocks) {
            BlockPos pos = queue.poll();

            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.relative(direction);
                if (!visited.contains(neighborPos) && level.getBlockState(neighborPos).is(targetBlock)) {
                    queue.add(neighborPos);
                    visited.add(neighborPos);
                }
            }
        }
        return visited;
    }


    public void cycleShadeless(Player player) {
        shadelessMode = !shadelessMode;
        sendMessage(player, "shadeless", shadelessMode);
    }

    public void cycleReinforced(Player player) {
        reinforcedMode = !reinforcedMode;
        sendMessage(player, "reinforced", reinforcedMode);
    }

    public void cycleLight(Player player) {
        switch (lightMode) {
            case NONE -> lightMode = CrystalixBlock.Light.LIGHT;
            case LIGHT -> lightMode = CrystalixBlock.Light.DARK;
            case DARK -> lightMode = CrystalixBlock.Light.NONE;
        }
        sendMessage(player, "light", lightMode.name().toLowerCase(Locale.ROOT));
    }

    public void cycleGhost(Player player) {
        switch (ghostMode) {
            case BLOCK_ALL -> ghostMode = CrystalixBlock.Ghost.ALLOW_ALL;
            case ALLOW_ALL -> ghostMode = CrystalixBlock.Ghost.BLOCK_PLAYER;
            case BLOCK_PLAYER -> ghostMode = CrystalixBlock.Ghost.ALLOW_PLAYER;
            case ALLOW_PLAYER -> ghostMode = CrystalixBlock.Ghost.BLOCK_MONSTER;
            case BLOCK_MONSTER -> ghostMode = CrystalixBlock.Ghost.ALLOW_MONSTER;
            case ALLOW_MONSTER -> ghostMode = CrystalixBlock.Ghost.BLOCK_ANIMAL;
            case BLOCK_ANIMAL -> ghostMode = CrystalixBlock.Ghost.ALLOW_ANIMAL;
            default -> ghostMode = CrystalixBlock.Ghost.BLOCK_ALL;
        }
        sendMessage(player, "ghost", ghostMode.name().toLowerCase());
    }

    private void sendMessage(Player player, String key, boolean state) {
        player.displayClientMessage(getTranslation(key, state), true);
    }

    private void sendMessage(Player player, String key, String state) {
        player.displayClientMessage(getTranslation(key, state), true);
    }

    private Component getTranslation(String key, boolean state) {
        return Component.translatable(String.format("tooltip.%s.wand.%s.%s", Crystalix.MOD_ID, key, state ? "enabled" : "disabled"));
    }

    private Component getTranslation(String key, String state) {
        return Component.translatable(String.format("tooltip.%s.wand.%s.%s", Crystalix.MOD_ID, key, state));
    }

    public boolean isShadeless() {
        return shadelessMode;
    }

    public boolean isReinforced() {
        return reinforcedMode;
    }

    public CrystalixBlock.Light getLightMode() {
        return lightMode;
    }

    public CrystalixBlock.Ghost getGhostMode() {
        return ghostMode;
    }
}
