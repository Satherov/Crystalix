package com.satherov.crystalix.content.item;

import java.util.*;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.CrystalixConfig;
import com.satherov.crystalix.content.CrystalixRegistry;
import com.satherov.crystalix.content.block.CrystalixGlass;
import com.satherov.crystalix.content.properties.BlockProperties;
import com.satherov.crystalix.content.properties.IProperty;

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

public class CrystalixWand extends Item {

    public CrystalixWand(Properties properties, boolean shadeless, boolean reinforced, BlockProperties.Light light, BlockProperties.Ghost ghost) {
        super(properties
                .stacksTo(1)
                .component(CrystalixRegistry.SHADELESS, shadeless)
                .component(CrystalixRegistry.REINFORCED, reinforced)
                .component(CrystalixRegistry.LIGHT, light)
                .component(CrystalixRegistry.GHOST, ghost));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable(String.format("%s.wand.bulk", Crystalix.MOD_ID)).withStyle(ChatFormatting.GRAY));
        Arrays.stream(new BlockProperties(stack).properties).toList().forEach(property -> tooltipComponents.add(property.toComponent()));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel() ;
        Player player = context.getPlayer();

        if (!level.isClientSide && player != null) {
            BlockPos blockpos = context.getClickedPos();
            if (!applyToBlock(level, blockpos, player)) {
                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide()) ;
    }

    public boolean applyToBlock(LevelAccessor accessor, BlockPos pos, Player player) {
        if (!(accessor.getBlockState(pos).getBlock() instanceof CrystalixGlass)) return false;
        ItemStack wand = player.getMainHandItem();

        Set<BlockPos> blocksToModify = player.isCrouching()
                ? getConnectedBlocks(accessor, pos, accessor.getBlockState(pos).getBlock(), CrystalixConfig.max_wand_edit)
                : Set.of(pos);

        for (BlockPos targetPos : blocksToModify) {
            BlockState newState = accessor.getBlockState(targetPos)
                    .setValue(CrystalixGlass.SHADELESS, Objects.requireNonNull(wand.get(CrystalixRegistry.SHADELESS)))
                    .setValue(CrystalixGlass.REINFORCED, Objects.requireNonNull(wand.get(CrystalixRegistry.REINFORCED)))
                    .setValue(CrystalixGlass.LIGHT, Objects.requireNonNull(wand.get(CrystalixRegistry.LIGHT)))
                    .setValue(CrystalixGlass.GHOST, Objects.requireNonNull(wand.get(CrystalixRegistry.GHOST)));

            accessor.setBlock(targetPos, newState, 3);
        }

        return true;
    }

    public Set<BlockPos> getConnectedBlocks(LevelAccessor accessor, BlockPos start, Block targetBlock, int maxBlocks) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty() && visited.size() < maxBlocks) {
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

    public static void sendMessage(Player player, IProperty<?> property) {
        player.displayClientMessage(property.toComponent(), true);
    }
}
