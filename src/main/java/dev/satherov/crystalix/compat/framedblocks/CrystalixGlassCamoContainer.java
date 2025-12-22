package dev.satherov.crystalix.compat.framedblocks;

import lombok.Getter;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import xfacthd.framedblocks.api.camo.block.AbstractBlockCamoContainer;
import xfacthd.framedblocks.api.camo.block.AbstractBlockCamoContainerFactory;

final class CrystalixGlassCamoContainer extends AbstractBlockCamoContainer<CrystalixGlassCamoContainer> {

    private final @Getter int tintColor;

    CrystalixGlassCamoContainer(BlockState state, int tintColor) {
        super(state);
        this.tintColor = tintColor;
    }

    @Override
    public int getTintColor(BlockAndTintGetter level, BlockPos pos, int tintIdx) {
        return tintColor;
    }

    @Override
    public int getTintColor(ItemStack stack, int tintIdx) {
        return tintColor;
    }

    @Override
    public Integer getBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return tintColor;
    }

    @Override
    public int hashCode() {
        return content.hashCode() * 31 + Integer.hashCode(tintColor);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof CrystalixGlassCamoContainer other)) return false;
        return content.equals(other.content) && tintColor == other.tintColor;
    }

    @Override
    public String toString() {
        return "CrystalixGlassCamoContainer{content=" + content + ",tintColor=" + Integer.toHexString(0xFF000000 | tintColor) + "}";
    }

    @Override
    public AbstractBlockCamoContainerFactory<CrystalixGlassCamoContainer> getFactory() {
        return CSFramedBlocksCompat.Guarded.FACTORY_CS_BLOCK.value();
    }
}
