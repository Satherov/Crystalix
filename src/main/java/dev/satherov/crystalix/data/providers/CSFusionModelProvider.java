package dev.satherov.crystalix.data.providers;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.common.block.CrystalixGlass;
import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

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
        CSRegistry.ENTRIES.forEach((type, holder) -> {
            this.addModel(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/" + holder.getId().getPath()),
                    ModelInstance.of(
                            DefaultModelTypes.CONNECTING,
                            ConnectingModelData.builder()
                                    .parent(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/block"))
                                    .texture("all", ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/" + type.format()))
                                    .connection(DefaultConnectionPredicates.isSameBlock().and(DefaultConnectionPredicates.matchState(holder.get(), Pair.of(CrystalixGlass.COLORED, false), Pair.of(CrystalixGlass.INVISIBLE, false))))
                                    .build()
                    )
            );
            this.addModel(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/" + holder.getId().getPath() + "_no_shade"),
                    ModelInstance.of(
                            DefaultModelTypes.CONNECTING,
                            ConnectingModelData.builder()
                                    .parent(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/no_shade_block"))
                                    .texture("all", ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/" + type.format()))
                                    .connection(DefaultConnectionPredicates.isSameBlock().and(DefaultConnectionPredicates.matchState(holder.get(), Pair.of(CrystalixGlass.COLORED, false), Pair.of(CrystalixGlass.INVISIBLE, false))))
                                    .build()
                    )
            );
            this.addModel(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/" + holder.getId().getPath() + "_colored"),
                    ModelInstance.of(
                            DefaultModelTypes.CONNECTING,
                            ConnectingModelData.builder()
                                    .parent(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/block"))
                                    .texture("all", ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/colored_" + type.format()))
                                    .connection(DefaultConnectionPredicates.isSameBlock().and(DefaultConnectionPredicates.matchState(holder.get(), Pair.of(CrystalixGlass.COLORED, true), Pair.of(CrystalixGlass.INVISIBLE, false))))
                                    .build()
                    )
            );
            this.addModel(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/" + holder.getId().getPath() + "_no_shade_colored"),
                    ModelInstance.of(
                            DefaultModelTypes.CONNECTING,
                            ConnectingModelData.builder()
                                    .parent(ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/no_shade_block"))
                                    .texture("all", ResourceLocation.fromNamespaceAndPath(Crystalix.MOD_ID, "block/colored_" + type.format()))
                                    .connection(DefaultConnectionPredicates.isSameBlock().and(DefaultConnectionPredicates.matchState(holder.get(), Pair.of(CrystalixGlass.COLORED, true), Pair.of(CrystalixGlass.INVISIBLE, false))))
                                    .build()
                    )
            );
        });
    }
}
