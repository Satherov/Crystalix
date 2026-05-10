package dev.satherov.crystalix.common.block;

import lombok.Getter;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.common.properties.CrystalixModelState;
import dev.satherov.crystalix.common.properties.GlassMaterial;
import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.sathlib.client.model.data.SLModelProperty;
import dev.satherov.sathlib.core.annotations.NothingNull;

import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.function.Consumer;

@NothingNull
public class CrystalixGlassBlockEntity extends BlockEntity {
    
    public static final ModelProperty<Integer> COLOR = new ModelProperty<>();
    public static final SLModelProperty<CrystalixModelState> MODEL_STATE = SLModelProperty.register(Crystalix.id("model_state"));
    
    private @Getter int color = 0xFFFFFF;
    private @Getter CrystalixModelState modelState = CrystalixModelState.empty();
    
    private @Getter boolean reinforced = false;
    private @Getter boolean waterloggable = false;
    private @Getter boolean conductor = false;
    private @Getter int redstone = 0;
    
    public CrystalixGlassBlockEntity(BlockPos pos, BlockState state) {
        super(CXRegistry.GLASS_BLOCK_ENTITY.get(), pos, state);
    }
    
    public void setMaterial(GlassMaterial material) {
        this.setter(this.modelState.getMaterial(), material, value -> this.modelState.setMaterial(value));
    }
    
    public GlassMaterial getMaterial() {
        return this.modelState.getMaterial();
    }
    
    public void setShadeless(boolean shadeless) {
        this.setter(this.modelState.isShadeless(), shadeless, value -> this.modelState.setShadeless(value));
    }
    
    public boolean isShadeless() {
        return this.modelState.isShadeless();
    }
    
    public void setTinted(boolean tinted) {
        this.setter(this.modelState.isTinted(), tinted, value -> this.modelState.setTinted(value));
    }
    
    public boolean isTinted() {
        return this.modelState.isTinted();
    }
    
    public void setColor(int color) {
        this.setter(this.color, color, value -> {
            this.color = value;
            this.updateVisual();
        });
    }
    
    public void setReinforced(boolean reinforced) {
        this.setter(this.reinforced, reinforced, value -> this.reinforced = value);
    }
    
    public static boolean isReinforced(BlockGetter getter, BlockPos pos) {
        return getter.getBlockEntity(pos) instanceof CrystalixGlassBlockEntity entity && entity.isReinforced();
    }
    
    public void setConductor(boolean conductor) {
        this.setter(this.conductor, conductor, value -> this.conductor = value);
    }
    
    public static BlockBehaviour.StatePredicate isRedstoneConductor() {
        return (_, getter, pos) -> getter.getBlockEntity(pos) instanceof CrystalixGlassBlockEntity entity && entity.isConductor();
    }
    
    public void setWaterloggable(boolean waterloggable) {
        this.setter(this.waterloggable, waterloggable, value -> this.waterloggable = value);
    }
    
    public static boolean isWaterloggable(BlockGetter getter, BlockPos pos) {
        return getter.getBlockEntity(pos) instanceof CrystalixGlassBlockEntity entity && entity.isWaterloggable();
    }
    
    public void setRedstone(int redstone) {
        int clamped = Math.clamp(redstone, 0, 15);
        this.setter(this.redstone, clamped, value -> this.redstone = value);
    }
    
    public static int getRedstone(BlockGetter getter, BlockPos pos) {
        if (getter.getBlockEntity(pos) instanceof CrystalixGlassBlockEntity entity) return entity.getRedstone();
        return 0;
    }
    
    private <T> void setter(T current, T next, Consumer<T> setter) {
        if (current == next) return;
        setter.accept(next);
        this.update();
    }
    
    private void updateVisual() {
        if (this.level == null) return;
        if (this.level.isClientSide()) this.refreshVisualModelData();
        this.update();
    }
    
    private void update() {
        if (this.level == null) return;
        this.setChanged();
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        this.notifyVisualNeighbors();
        this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
    }
    
    private void refreshVisualModelData() {
        this.requestModelDataUpdate();
        if (this.level == null) return;
        for (Direction direction : Direction.values()) {
            if (this.level.getBlockEntity(this.worldPosition.relative(direction)) instanceof CrystalixGlassBlockEntity entity) {
                entity.requestModelDataUpdate();
            }
        }
    }
    
    private void notifyVisualNeighbors() {
        if (this.level == null) return;
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = this.worldPosition.relative(direction);
            BlockState neighborState = this.level.getBlockState(neighborPos);
            if (!neighborState.is(this.getBlockState().getBlock())) continue;
            this.level.sendBlockUpdated(neighborPos, neighborState, neighborState, Block.UPDATE_CLIENTS);
        }
    }
    
    @Override
    protected void saveAdditional(ValueOutput output) {
        output.putInt("color", this.color);
        output.store("model_state", CrystalixModelState.CODEC, this.modelState);
        
        output.putBoolean("reinforced", this.reinforced);
        output.putBoolean("waterloggable", this.waterloggable);
        output.putBoolean("conductor", this.conductor);
        output.putInt("redstone", this.redstone);
    }
    
    @Override
    protected void loadAdditional(ValueInput input) {
        this.color = input.getIntOr("color", 0xFFFFFF);
        this.modelState = input.read("model_state", CrystalixModelState.CODEC).orElse(CrystalixModelState.empty());
        
        this.reinforced = input.getBooleanOr("reinforced", false);
        this.waterloggable = input.getBooleanOr("waterloggable", false);
        this.conductor = input.getBooleanOr("conductor", false);
        this.redstone = input.getIntOr("redstone", 0);
        
        if (this.level != null && this.level.isClientSide()) {
            this.refreshVisualModelData();
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
            this.notifyVisualNeighbors();
        }
    }
    
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }
    
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(CrystalixGlassBlockEntity.MODEL_STATE.property(), this.modelState)
                .with(CrystalixGlassBlockEntity.COLOR, this.color)
                .build();
    }
}
