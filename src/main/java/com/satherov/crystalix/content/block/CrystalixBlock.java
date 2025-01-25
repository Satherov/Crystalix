package com.satherov.crystalix.content.block;

import java.util.List;
import org.jetbrains.annotations.Nullable;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.CrystalixTags;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

public class CrystalixBlock extends TransparentBlock implements BeaconBeamBlock, LiquidBlockContainer {

    private final DyeColor color;

    public CrystalixBlock(DyeColor dyeColor, BlockBehaviour.Properties properties) {
        super(properties);
        this.color = dyeColor;
    }

    @Override
    public DyeColor getColor() {
        return this.color;
    }

    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        if (stack.getItem().getDefaultInstance().is(CrystalixTags.ITEMTAG_SHADED)) {
            tooltipComponents.add(Component.translatable(Crystalix.MOD_ID + ".tooltip.shadeless.enabled"));
        } else {
            tooltipComponents.add(Component.translatable(Crystalix.MOD_ID + ".tooltip.shadeless.disabled"));
        }
        if (stack.getItem().getDefaultInstance().is(CrystalixTags.ITEMTAG_REINFORCED)) {
            tooltipComponents.add(Component.translatable(Crystalix.MOD_ID + ".tooltip.reinforced.enabled"));
        } else {
            tooltipComponents.add(Component.translatable(Crystalix.MOD_ID + ".tooltip.reinforced.disabled"));
        }
        if (stack.getItem().getDefaultInstance().is(CrystalixTags.ITEMTAG_LIGHT)) {
            tooltipComponents.add(Component.translatable(Crystalix.MOD_ID + ".tooltip.light.enabled"));
        } else {
            tooltipComponents.add(Component.translatable(Crystalix.MOD_ID + ".tooltip.light.disabled"));
        }
        tooltipComponents.add(Component.translatable(Crystalix.MOD_ID + ".tooltip.dark.disabled"));
        if (stack.getItem().getDefaultInstance().is(CrystalixTags.ITEMTAG_GHOST)) {
            tooltipComponents.add(Component.translatable(Crystalix.MOD_ID + ".tooltip.ghost.enabled"));
        } else {
            tooltipComponents.add(Component.translatable(Crystalix.MOD_ID + ".tooltip.ghost.disabled"));
        }

    }

    @Override
    public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        return false;
    }
}
