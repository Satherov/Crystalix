package dev.satherov.crystalix.common.block;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import dev.satherov.crystalix.core.CSRegistry;
import dev.satherov.crystalix.core.annotations.NothingNull;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

@Slf4j
@NothingNull
public class CrystalixGlassTile extends BlockEntity {
    
    private static final ModelProperty<Integer> COLOR = new ModelProperty<>();
    
    private @Getter int color = -1;
    
    public CrystalixGlassTile(BlockPos pos, BlockState blockState) {
        super(CSRegistry.GLASS_TILE.get(), pos, blockState);
    }
    
    public void setColor(BlockState state, int color) {
        if (this.color != color) {
            this.color = color;
            this.setChanged();
            if (this.level != null) {
                if (this.level.isClientSide()) {
                    requestModelDataUpdate();
                } else {
                    this.level.setBlock(worldPosition, state.setValue(CrystalixGlass.COLORED, color != -1), Block.UPDATE_ALL_IMMEDIATE);
                    this.level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
                }
            }
        }
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("color", color);
    }
    
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("color")) color = tag.getInt("color");
    }
    
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
    
    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider registries) {
        super.onDataPacket(connection, packet, registries);
        requestModelDataUpdate();
        if (FMLLoader.getDist() == Dist.CLIENT && level != null && level.isClientSide()) {
            Minecraft.getInstance().levelRenderer.blockChanged(level, worldPosition, getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
        }
    }
    
    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(COLOR, color).build();
    }
}
