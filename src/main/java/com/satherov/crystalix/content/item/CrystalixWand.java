package com.satherov.crystalix.content.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import com.satherov.crystalix.CrystalixConfig;
import com.satherov.crystalix.content.block.CrystalixGlass;
import com.satherov.crystalix.content.properties.BlockProperties;
import com.satherov.crystalix.content.properties.ITranslatableProperty;
import com.satherov.crystalix.core.CrystalixRegistry;
import com.satherov.crystalix.core.annotations.NothingNull;
import com.satherov.crystalix.core.lang.CrystalixLanguage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

@NothingNull
public class CrystalixWand extends Item {

    public CrystalixWand(Properties properties) {
        super(properties
                .stacksTo(1)
                .component(CrystalixRegistry.INVISIBLE, false)
                .component(CrystalixRegistry.SHADELESS, false)
                .component(CrystalixRegistry.REINFORCED, false)
                .component(CrystalixRegistry.LIGHT, BlockProperties.Light.NONE)
                .component(CrystalixRegistry.GHOST, BlockProperties.Ghost.BLOCK_ALL));
    }

    public static void sendMessage(Player player, ITranslatableProperty<?> property) {
        player.displayClientMessage(property.getTranslation(), true);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(CrystalixLanguage.TOOLTIP_BULK.translateFormatted(ChatFormatting.GRAY));
        Arrays.stream(new BlockProperties(stack).properties).toList().forEach(property -> tooltipComponents.add(property.getTranslation()));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();

        if (player != null && !level.isClientSide) {
            BlockPos blockpos = context.getClickedPos();
            if (!applyToBlock(level, blockpos, player)) {
                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    public boolean applyToBlock(LevelAccessor accessor, BlockPos pos, Player player) {
        if (!(accessor.getBlockState(pos).getBlock() instanceof CrystalixGlass)) return false;
        ItemStack wand = player.getMainHandItem();

        Set<BlockPos> blocksToModify = player.isCrouching()
                ? getConnectedBlocks(accessor, pos, accessor.getBlockState(pos).getBlock())
                : Set.of(pos);

        for (BlockPos targetPos : blocksToModify) {
            BlockState newState = accessor.getBlockState(targetPos)
                    .setValue(CrystalixGlass.INVISIBLE, Objects.requireNonNull(wand.get(CrystalixRegistry.INVISIBLE)))
                    .setValue(CrystalixGlass.SHADELESS, Objects.requireNonNull(wand.get(CrystalixRegistry.SHADELESS)))
                    .setValue(CrystalixGlass.REINFORCED, Objects.requireNonNull(wand.get(CrystalixRegistry.REINFORCED)))
                    .setValue(CrystalixGlass.LIGHT, Objects.requireNonNull(wand.get(CrystalixRegistry.LIGHT)))
                    .setValue(CrystalixGlass.GHOST, Objects.requireNonNull(wand.get(CrystalixRegistry.GHOST)));

            accessor.setBlock(targetPos, newState, 3);
        }

        return true;
    }

    public Set<BlockPos> getConnectedBlocks(LevelAccessor accessor, BlockPos start, Block targetBlock) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty() && visited.size() < CrystalixConfig.max_wand_edit) {
            BlockPos pos = queue.poll();

            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.relative(direction);
                if (!visited.contains(neighborPos) && accessor.getBlockState(neighborPos).is(targetBlock)) {
                    queue.add(neighborPos);
                    visited.add(neighborPos);
                }
            }
        }
        return visited;
    }
}
