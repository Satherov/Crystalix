package dev.satherov.crystalix.common.block;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import dev.satherov.crystalix.core.CSRegistry;
import dev.satherov.crystalix.core.annotations.NothingNull;

import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

@Slf4j
@NothingNull
public class CrystalixGlassTile extends BlockEntity {
    
    private static final ModelProperty<Integer> COLOR = new ModelProperty<>();
    
    private @Getter int color = 0xFFFFFF;
    private @Getter boolean reinforced = false;
    private @Getter boolean conductor = false;
    
    public CrystalixGlassTile(BlockPos pos, BlockState blockState) {
        super(CSRegistry.GLASS_TILE.get(), pos, blockState);
    }
    
    public void setColor(BlockState state, int color) {
        if (this.color == color) return;
        
        this.color = color;
        this.setAndUpdate(state);
    }
    
    public void setReinforced(boolean reinforced) {
        if (this.reinforced == reinforced) return;
        
        this.reinforced = reinforced;
        this.setAndUpdate(this.getBlockState());
    }
    
    public void setConductor(boolean conductor) {
        if (this.conductor == conductor) return;
        
        this.conductor = conductor;
        this.setAndUpdate(this.getBlockState());
    }
    
    private void setAndUpdate(BlockState state) {
        this.setChanged();
        if (this.level == null) return;
        this.level.setBlock(this.worldPosition, state, Block.UPDATE_ALL_IMMEDIATE);
        this.level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_ALL);
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("color", this.color);
        tag.putBoolean("reinforced", this.reinforced);
        tag.putBoolean("conductor", this.conductor);
    }
    
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("color")) this.color = tag.getInt("color");
        if (tag.contains("reinforced")) this.reinforced = tag.getBoolean("reinforced");
        if (tag.contains("conductor")) this.conductor = tag.getBoolean("conductor");
    }
    
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }
    
    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
        super.onDataPacket(connection, packet, registries);
        if (this.level != null && this.level.isClientSide()) {
            Minecraft.getInstance().levelRenderer.blockChanged(this.level, this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_IMMEDIATE);
        }
    }
    
    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(CrystalixGlassTile.COLOR, this.color).build();
    }
    
    public static BlockBehaviour.StatePredicate isRedstoneConductor() {
        return (state, getter, pos) -> getter.getBlockEntity(pos) instanceof CrystalixGlassTile tile && tile.isConductor();
    }
    
    public static boolean isReinforced(BlockGetter getter, BlockPos pos) {
        return getter.getBlockEntity(pos) instanceof CrystalixGlassTile tile && tile.isReinforced();
    }
}
