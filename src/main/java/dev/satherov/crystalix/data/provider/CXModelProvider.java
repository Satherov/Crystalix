package dev.satherov.crystalix.data.provider;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.common.block.CrystalixGlassBlock;
import dev.satherov.crystalix.common.block.CrystalixGlassBlockEntity;
import dev.satherov.crystalix.common.properties.CrystalixModelState;
import dev.satherov.crystalix.common.properties.GlassMaterial;
import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.sathlib.client.model.connected.ConnectionRules;
import dev.satherov.sathlib.core.annotations.NothingNull;
import dev.satherov.sathlib.data.model.SLBlockModelGenerators;
import dev.satherov.sathlib.data.model.SLConditionalModelBuilder;
import dev.satherov.sathlib.data.model.SLConnectedTextureModelBuilder;
import dev.satherov.sathlib.data.model.SLModelProvider;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

@NothingNull
public class CXModelProvider extends SLModelProvider {
    
    public CXModelProvider(PackOutput output) {
        super(output, Crystalix.MOD_ID);
    }
    
    @Override
    protected void registerModels(SLBlockModelGenerators blocks, ItemModelGenerators items) {
        blocks.registerMultiVariant(CXRegistry.CRYSTALIX_BLOCK, MultiVariant.of(this.generateGlassModel()));
        blocks.registerSimpleItemModel(CXRegistry.CRYSTALIX_BLOCK.get(), this.connectedGlassModel(GlassMaterial.NORMAL, false, false).createItem(blocks, CXRegistry.CRYSTALIX_BLOCK));
        items.generateFlatItem(CXRegistry.CRYSTALIX_WAND.get(), ModelTemplates.FLAT_ITEM);
    }
    
    private SLConditionalModelBuilder generateGlassModel() {
        SLConditionalModelBuilder glassModel = SLConditionalModelBuilder.conditional(this.connectedGlassModel(GlassMaterial.NORMAL, false, false));
        
        for (GlassMaterial material : GlassMaterial.values()) {
            for (boolean shadeless : new boolean[] { false, true }) {
                for (boolean tinted : new boolean[] { false, true }) {
                    if (material == GlassMaterial.NORMAL && !shadeless && !tinted) continue;
                    
                    CrystalixModelState state = new CrystalixModelState(shadeless, tinted, material);
                    glassModel = material.connected() ?
                            glassModel.whenModelProperty(
                                    CrystalixGlassBlockEntity.MODEL_STATE,
                                    state,
                                    this.connectedGlassModel(material, shadeless, tinted)
                            ) :
                            glassModel.whenModelProperty(
                                    CrystalixGlassBlockEntity.MODEL_STATE,
                                    state,
                                    this.createId(material, shadeless, tinted)
                            );
                }
            }
        }
        
        return glassModel;
    }
    
    private SLConnectedTextureModelBuilder connectedGlassModel(GlassMaterial material, boolean shadeless, boolean tinted) {
        return SLConnectedTextureModelBuilder.connected(
                BlockModelGenerators.plainModel(this.createId(material, shadeless, tinted))
        ).connect(
                material.sprite(tinted),
                ConnectionRules.all(
                        ConnectionRules.sameBlock(),
                        ConnectionRules.not(ConnectionRules.neighborStateProperty(CrystalixGlassBlock.INVISIBLE, true)),
                        ConnectionRules.sameModelProperty(CrystalixGlassBlockEntity.MODEL_STATE, CrystalixModelState.MATERIAL),
                        ConnectionRules.sameModelProperty(CrystalixGlassBlockEntity.MODEL_STATE, CrystalixModelState.TINTED)
                )
        );
    }
    
    private Identifier createId(GlassMaterial material, boolean shadeless, boolean tinted) {
        StringBuilder path = new StringBuilder("block/crystalix_glass_").append(material.getSerializedName());
        if (tinted) path.append("_colored");
        if (shadeless) path.append("_shadeless");
        return Crystalix.id(path.toString());
    }
}