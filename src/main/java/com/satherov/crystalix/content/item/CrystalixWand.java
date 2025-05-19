package com.satherov.crystalix.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.CrystalixConfig;
import com.satherov.crystalix.content.CrystalixRegistry;
import com.satherov.crystalix.content.block.CrystalixGlass;
import com.satherov.crystalix.content.properties.BlockProperties;
import com.satherov.crystalix.content.properties.IProperty;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class CrystalixWand extends Item {

    public CrystalixWand(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static void sendMessage(Player player, IProperty<?> property) {
        player.displayClientMessage(property.toComponent(), true);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        CompoundTag tag = stack.getOrCreateTag();

        tag.putBoolean(CrystalixRegistry.SHADELESS, false);
        tag.putBoolean(CrystalixRegistry.REINFORCED, false);
        tag.putString(CrystalixRegistry.LIGHT, BlockProperties.Light.NONE.getSerializedName());
        tag.putString(CrystalixRegistry.GHOST, BlockProperties.Ghost.BLOCK_ALL.getSerializedName());
        stack.setTag(tag);

        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level pLevel, List<Component> tooltipComponents, TooltipFlag pIsAdvanced) {
        tooltipComponents.add(Component.translatable(Crystalix.MOD_ID + ".wand.bulk").withStyle(ChatFormatting.GRAY));
        BlockProperties props = new BlockProperties(stack);
        Arrays.stream(props.properties)
                .forEach(p -> tooltipComponents.add(p.toComponent()));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();

        if (player != null && !level.isClientSide) {
            BlockPos pos = context.getClickedPos();
            if (!applyToBlock(level, pos, player)) {
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    public boolean applyToBlock(LevelAccessor accessor, BlockPos pos, Player player) {
        if (!(accessor.getBlockState(pos).getBlock() instanceof CrystalixGlass)) {
            return false;
        }
        ItemStack wandStack = player.getMainHandItem();
        BlockProperties props = new BlockProperties(wandStack);

        Set<BlockPos> blocksToModify = player.isCrouching()
                ? getConnectedBlocks(accessor, pos, accessor.getBlockState(pos).getBlock(), CrystalixConfig.COMMON_CONFIG.maxWandEdit.get())
                : Set.of(pos);

        for (BlockPos target : blocksToModify) {
            BlockState original = accessor.getBlockState(target);
            BlockState updated = original
                    .setValue(CrystalixGlass.SHADELESS, props.shadeless.get())
                    .setValue(CrystalixGlass.REINFORCED, props.reinforced.get())
                    .setValue(CrystalixGlass.LIGHT, props.light.get())
                    .setValue(CrystalixGlass.GHOST, props.ghost.get());

            accessor.setBlock(target, updated, 3);
        }
        return true;
    }

    private Set<BlockPos> getConnectedBlocks(LevelAccessor accessor, BlockPos start, Object block, int limit) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty() && visited.size() < limit) {
            BlockPos current = queue.poll();
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.relative(dir);
                if (!visited.contains(neighbor) && accessor.getBlockState(neighbor).getBlock() == block) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return visited;
    }
}
