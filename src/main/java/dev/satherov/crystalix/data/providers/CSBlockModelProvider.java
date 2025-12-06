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
                            this.modLoc("block/block"),
                            "all", this.modLoc("block/colored_" + type.format())
                    )
                    .renderType(RenderType.translucent().name);
            
            this.singleTexture("block/" + holder.getId().getPath() + "_no_shade_colored",
                            this.modLoc("block/no_shade_block"),
                            "all", this.modLoc("block/colored_" + type.format())
                    )
                    .renderType(RenderType.translucent().name);
            
            this.singleTexture("block/" + holder.getId().getPath(),
                            this.modLoc("block/block"),
                            "all", this.modLoc("block/" + type.format())
                    )
                    .renderType(RenderType.translucent().name);
            
            this.singleTexture("block/" + holder.getId().getPath() + "_no_shade",
                            this.modLoc("block/no_shade_block"),
                            "all", this.modLoc("block/" + type.format())
                    )
                    .renderType(RenderType.translucent().name);
        });
    }
}
