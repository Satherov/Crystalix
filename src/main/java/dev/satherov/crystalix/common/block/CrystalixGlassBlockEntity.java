package dev.satherov.crystalix.common.block;

import lombok.Getter;

import dev.satherov.crystalix.core.registry.CXRegistry;
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

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.ints.IntConsumer;

@Getter
@NothingNull
public class CrystalixGlassBlockEntity extends BlockEntity {
    
    public static final ModelProperty<Integer> COLOR = new ModelProperty<>();
    
    private int color = 0xFFFFFF;
    
    private boolean reinforced = false;
    private boolean waterloggable = false;
    private boolean conductor = false;
    private int redstone = 0;
    
    public CrystalixGlassBlockEntity(BlockPos pos, BlockState state) {
        super(CXRegistry.GLASS_BLOCK_ENTITY.get(), pos, state);
    }
    
    public void setColor(int color) {
        this.setVisualInt(this.color, color, value -> this.color = value);
    }
    
    public int getColor() {
        return this.color;
    }
    
    public void setReinforced(boolean reinforced) {
        this.setStateBoolean(this.reinforced, reinforced, value -> this.reinforced = value);
    }
    
    public boolean isReinforced() {
        return this.reinforced;
    }
    
    public static boolean isReinforced(BlockGetter getter, BlockPos pos) {
        return getter.getBlockEntity(pos) instanceof CrystalixGlassBlockEntity entity && entity.isReinforced();
    }
    
    public void setConductor(boolean conductor) {
        this.setStateBoolean(this.conductor, conductor, value -> this.conductor = value);
    }
    
    public boolean isConductor() {
        return this.conductor;
    }
    
    public static BlockBehaviour.StatePredicate isRedstoneConductor() {
        return (_, getter, pos) -> getter.getBlockEntity(pos) instanceof CrystalixGlassBlockEntity entity && entity.isConductor();
    }
    
    public void setWaterloggable(boolean waterloggable) {
        this.setStateBoolean(this.waterloggable, waterloggable, value -> this.waterloggable = value);
    }
    
    public boolean isWaterloggable() {
        return this.waterloggable;
    }
    
    public static boolean isWaterloggable(BlockGetter getter, BlockPos pos) {
        return getter.getBlockEntity(pos) instanceof CrystalixGlassBlockEntity entity && entity.isWaterloggable();
    }
    
    public void setRedstone(int redstone) {
        int clamped = Math.clamp(redstone, 0, 15);
        this.setStateInt(this.redstone, clamped, value -> this.redstone = value);
    }
    
    public int getRedstone() {
        return this.redstone;
    }
    
    public static int getRedstone(BlockGetter getter, BlockPos pos) {
        if (getter.getBlockEntity(pos) instanceof CrystalixGlassBlockEntity entity) return entity.getRedstone();
        return 0;
    }
    
    private void setStateBoolean(boolean current, boolean next, BooleanConsumer setter) {
        if (current == next) return;
        setter.accept(next);
        this.update();
    }
    
    private void setVisualInt(int current, int next, IntConsumer setter) {
        if (current == next) return;
        setter.accept(next);
        this.updateVisual();
    }
    
    private void setStateInt(int current, int next, IntConsumer setter) {
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
        
        output.putBoolean("reinforced", this.reinforced);
        output.putBoolean("waterloggable", this.waterloggable);
        output.putBoolean("conductor", this.conductor);
        output.putInt("redstone", this.redstone);
    }
    
    @Override
    protected void loadAdditional(ValueInput input) {
        this.color = input.getIntOr("color", 0xFFFFFF);
        
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
                .with(CrystalixGlassBlockEntity.COLOR, this.color)
                .build();
    }
}
