package dev.satherov.crystalix.compat.framedblocks;

import dev.satherov.sathlib.core.annotations.NothingNull;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import io.github.xfacthd.framedblocks.api.camo.CamoContainerClientHandler;
import io.github.xfacthd.framedblocks.api.camo.block.BlockCamoContent;
import it.unimi.dsi.fastutil.ints.IntList;

@NothingNull
public final class CrystalixGlassCamoContainerClientHandler extends CamoContainerClientHandler<BlockCamoContent, CrystalixGlassCamoContainer> {
    
    public static final CamoContainerClientHandler<BlockCamoContent, CrystalixGlassCamoContainer> INSTANCE = new CrystalixGlassCamoContainerClientHandler();
    
    @Override
    public int getTintCount(CrystalixGlassCamoContainer container) {
        return container.getTintColor();
    }
    
    @Override
    public void collectTintValues(CrystalixGlassCamoContainer container, BlockAndTintGetter blockAndTintGetter, BlockPos blockPos, IntList intList) {
        intList.add(container.getTintColor() | 0xFF000000);
    }
    
    @Override
    public void collectTintValues(CrystalixGlassCamoContainer container, ItemStack itemStack, IntList intList) {
        intList.add(container.getTintColor() | 0xFF000000);
    }
}
