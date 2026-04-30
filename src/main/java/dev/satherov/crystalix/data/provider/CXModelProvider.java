package dev.satherov.crystalix.data.provider;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.common.block.CrystalixGlassBlock;
import dev.satherov.crystalix.common.properties.GlassMaterial;
import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.sathlib.client.model.connected.SLConnectedTextureModelBuilder;
import dev.satherov.sathlib.client.model.connected.SLConnectedTextureRules;
import dev.satherov.sathlib.core.annotations.NothingNull;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

@NothingNull
public class CXModelProvider extends ModelProvider {
    
    public CXModelProvider(PackOutput output) {
        super(output, Crystalix.MOD_ID);
    }
    
    @Override
    protected void registerModels(BlockModelGenerators blocks, ItemModelGenerators items) {
        blocks.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(CXRegistry.CRYSTALIX_BLOCK.get()).with(
                        PropertyDispatch.initial(CrystalixGlassBlock.MATERIAL, CrystalixGlassBlock.SHADELESS, CrystalixGlassBlock.TINTED)
                                .generate((material, shadeless, tinted) -> {
                                    if (!material.connected()) return BlockModelGenerators.plainVariant(this.blockModel(material, shadeless, tinted));
                                    return MultiVariant.of(this.connectedGlassModel(material, shadeless, tinted));
                                })
                )
        );
        
        Identifier defaultItemModel = this.connectedGlassModel(GlassMaterial.NORMAL, false, false).createItemModel(blocks, CXRegistry.CRYSTALIX_BLOCK.get());
        blocks.registerSimpleItemModel(CXRegistry.CRYSTALIX_BLOCK.get(), defaultItemModel);
        
        Identifier wandModel = items.createFlatItemModel(CXRegistry.CRYSTALIX_WAND.get(), ModelTemplates.FLAT_ITEM);
        items.itemModelOutput.accept(CXRegistry.CRYSTALIX_WAND.get(), ItemModelUtils.plainModel(wandModel));
    }
    
    private SLConnectedTextureModelBuilder connectedGlassModel(GlassMaterial material, boolean shadeless, boolean tinted) {
        return SLConnectedTextureModelBuilder.connected(
                BlockModelGenerators.plainModel(this.blockModel(material, shadeless, tinted))
        ).connect(
                material.sprite(tinted),
                SLConnectedTextureRules.all(
                        SLConnectedTextureRules.sameBlock(),
                        SLConnectedTextureRules.not(SLConnectedTextureRules.neighborState(CrystalixGlassBlock.INVISIBLE, true)),
                        SLConnectedTextureRules.sameState(CrystalixGlassBlock.MATERIAL),
                        SLConnectedTextureRules.sameState(CrystalixGlassBlock.TINTED)
                )
        );
    }
    
    private Identifier blockModel(GlassMaterial material, boolean shadeless, boolean tinted) {
        StringBuilder path = new StringBuilder("block/crystalix_glass_").append(material.getSerializedName());
        if (tinted) path.append("_colored");
        if (shadeless) path.append("_shadeless");
        return Crystalix.id(path.toString());
    }
}