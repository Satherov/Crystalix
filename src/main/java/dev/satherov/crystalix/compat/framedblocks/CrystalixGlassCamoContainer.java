package dev.satherov.crystalix.compat.framedblocks;

import lombok.Getter;

import dev.satherov.sathlib.core.annotations.NothingNull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import io.github.xfacthd.framedblocks.api.camo.CamoContainerClientHandler;
import io.github.xfacthd.framedblocks.api.camo.block.AbstractBlockCamoContainer;
import io.github.xfacthd.framedblocks.api.camo.block.AbstractBlockCamoContainerFactory;
import io.github.xfacthd.framedblocks.api.camo.block.BlockCamoContent;

@NothingNull
public class CrystalixGlassCamoContainer extends AbstractBlockCamoContainer<CrystalixGlassCamoContainer> {
    
    private final @Getter int tintColor;
    
    protected CrystalixGlassCamoContainer(BlockState state, int tintColor) {
        super(state);
        this.tintColor = tintColor;
    }
    
    @Override
    public Integer getBeaconColorMultiplier(LevelReader level, BlockPos pos, BlockPos beaconPos) {
        return this.tintColor;
    }
    
    @Override
    public int hashCode() {
        return this.content.hashCode() * 31 + Integer.hashCode(this.tintColor);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof CrystalixGlassCamoContainer other)) return false;
        return this.content.equals(other.content) && this.tintColor == other.tintColor;
    }
    
    @Override
    public String toString() {
        return "CrystalixGlassCamoContainer{content=" + this.content + ",tintColor=" + Integer.toHexString(0xFF000000 | this.tintColor) + "}";
    }
    
    @Override
    public AbstractBlockCamoContainerFactory<CrystalixGlassCamoContainer> getFactory() {
        return CXFramedBlocksCompat.CRYSTALIX_GLASS_CAMO_FACTORY.get();
    }
    
    @Override
    public CamoContainerClientHandler<BlockCamoContent, CrystalixGlassCamoContainer> getClientHandler() {
        return CrystalixGlassCamoContainerClientHandler.INSTANCE;
    }
}
