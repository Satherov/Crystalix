package dev.satherov.crystalix.data.providers;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.PackOutput;

public class CSBlockModelProvider extends BlockModelProvider {
    
    public CSBlockModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Crystalix.MOD_ID, existingFileHelper);
    }
    
    @Override
    protected void registerModels() {
        CSRegistry.ENTRIES.forEach((type, holder) -> {
            this.singleTexture("block/" + holder.getId().getPath() + "_colored",
                            modLoc("block/block"),
                            "all", modLoc("block/colored_" + type.format())
                    )
                    .renderType(RenderType.translucent().name);
            
            this.singleTexture("block/" + holder.getId().getPath() + "_no_shade_colored",
                            modLoc("block/no_shade_block"),
                            "all", modLoc("block/colored_" + type.format())
                    )
                    .renderType(RenderType.translucent().name);
            
            this.singleTexture("block/" + holder.getId().getPath(),
                            modLoc("block/block"),
                            "all", modLoc("block/" + type.format())
                    )
                    .renderType(RenderType.translucent().name);
            
            this.singleTexture("block/" + holder.getId().getPath() + "_no_shade",
                            modLoc("block/no_shade_block"),
                            "all", modLoc("block/" + type.format())
                    )
                    .renderType(RenderType.translucent().name);
        });
    }
}
