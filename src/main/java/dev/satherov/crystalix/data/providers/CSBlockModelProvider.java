package dev.satherov.crystalix.data.providers;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

public class CSBlockModelProvider extends BlockModelProvider {
    
    public CSBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Crystalix.MOD_ID, existingFileHelper);
    }
    
    @Override
    protected void registerModels() {
        CSRegistry.ENTRIES.cellSet().forEach((cell) -> {
            shadedBlock(cell.getValue(), cell.getColumnKey(), cell.getRowKey());
            shadelessBlock(cell.getValue(), cell.getColumnKey(), cell.getRowKey());
        });
    }
    
    private void shadedBlock(DeferredHolder<Block, ? extends Block> block, CSRegistry.Colors color, CSRegistry.Types type) {
        this.singleTexture("block/" + type.getSerializedName() + "/" + block.getId().getPath(),
                           modLoc("block/block"),
                           "all", modLoc("block/" + type.getSerializedName() + "/" + (color.color() < 0 ? color.key() : "color"))
                )
                .renderType(RenderType.translucent().name);
    }
    
    private void shadelessBlock(DeferredHolder<Block, ? extends Block> block, CSRegistry.Colors color, CSRegistry.Types type) {
        this.singleTexture("block/" + type.getSerializedName() + "/" + block.getId().getPath() + "_no_shade",
                           modLoc("block/no_shade_block"),
                           "all", modLoc("block/" + type.getSerializedName() + "/" + (color.color() < 0 ? color.key() : "color"))
                )
                .renderType(RenderType.translucent().name);
    }
}
