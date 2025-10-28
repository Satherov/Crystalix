package dev.satherov.crystalix.data.providers;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.common.block.CrystalixGlass;
import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import com.supermartijn642.fusion.api.model.DefaultModelTypes;
import com.supermartijn642.fusion.api.model.ModelInstance;
import com.supermartijn642.fusion.api.model.data.ConnectingModelData;
import com.supermartijn642.fusion.api.predicate.DefaultConnectionPredicates;
import com.supermartijn642.fusion.api.provider.FusionModelProvider;
import com.supermartijn642.fusion.api.util.Pair;

import java.nio.file.Path;

@SuppressWarnings("unchecked")
public class CSFusionModelProvider extends FusionModelProvider {
    
    public CSFusionModelProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
        super(Crystalix.MOD_ID, new PackOutput(Path.of(packOutput.getOutputFolder().toAbsolutePath().toString(), "crystalix-fusion-overrides")), existingFileHelper);
    }
    
    @Override
    protected void generate() {
        CSRegistry.ENTRIES.cellSet().forEach((cell) -> {
            shadedBlock(cell.getValue(), cell.getColumnKey(), cell.getRowKey());
            shadelessBlock(cell.getValue(), cell.getColumnKey(), cell.getRowKey());
        });
    }
    
    private void shadedBlock(DeferredHolder<Block, ? extends Block> block, CSRegistry.Colors color, CSRegistry.Types type) {
        this.addModel(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/" + type.getSerializedName() + "/" + block.getId().getPath()),
                      ModelInstance.of(
                              DefaultModelTypes.CONNECTING,
                              ConnectingModelData.builder()
                                      .parent(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/block"))
                                      .texture("all", ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/" + type.getSerializedName() + "/" + (color.color() < 0 ? color.key() : "color")))
                                      .connection(DefaultConnectionPredicates.isSameBlock().and(DefaultConnectionPredicates.matchState(block.get(), Pair.of(CrystalixGlass.INVISIBLE, false))))
                                      .build()
                      )
        );
    }
    
    private void shadelessBlock(DeferredHolder<Block, ? extends Block> block, CSRegistry.Colors color, CSRegistry.Types type) {
        this.addModel(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/" + type.getSerializedName() + "/" + block.getId().getPath() + "_no_shade"),
                      ModelInstance.of(
                              DefaultModelTypes.CONNECTING,
                              ConnectingModelData.builder()
                                      .parent(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/no_shade_block"))
                                      .texture("all", ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/" + type.getSerializedName() + "/" + (color.color() < 0 ? color.key() : "color")))
                                      .connection(DefaultConnectionPredicates.isSameBlock().and(DefaultConnectionPredicates.matchState(block.get(), Pair.of(CrystalixGlass.INVISIBLE, false))))
                                      .build()
                      )
        );
    }
}
