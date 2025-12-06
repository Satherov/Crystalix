package dev.satherov.crystalix.core.network;

import lombok.extern.slf4j.Slf4j;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.common.block.CrystalixGlass;
import dev.satherov.crystalix.common.block.CrystalixGlassTile;
import dev.satherov.crystalix.common.item.CrystalixWand;
import dev.satherov.crystalix.common.properties.CSBooleanProperty;
import dev.satherov.crystalix.common.properties.CSEnumProperty;
import dev.satherov.crystalix.common.properties.CSIntegerProperty;
import dev.satherov.crystalix.common.properties.CSProperties;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

@Slf4j
public record SwapPropertiesPayload(BlockPos pos) implements CustomPacketPayload {
    
    public static final StreamCodec<RegistryFriendlyByteBuf, SwapPropertiesPayload> STREAM_CODEC = CustomPacketPayload.codec(
            SwapPropertiesPayload::encode,
            SwapPropertiesPayload::new
    );
    
    public static final Type<SwapPropertiesPayload> TYPE = new Type<>(Crystalix.rl("swap_properties"));
    
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return SwapPropertiesPayload.TYPE;
    }
    
    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.pos);
    }
    
    private SwapPropertiesPayload(RegistryFriendlyByteBuf buffer) {
        this(buffer.readBlockPos());
    }
    
    @SuppressWarnings("unchecked")
    public static void handle(final SwapPropertiesPayload message, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.flow().isServerbound() && ctx.player() instanceof ServerPlayer player) {
                ServerLevel level = player.serverLevel();
                ItemStack wand = CrystalixWand.find(player);
                if (wand.isEmpty()) return;
                BlockPos pos = message.pos();
                BlockState state = level.getBlockState(pos);
                if (!(state.getBlock() instanceof CrystalixGlass)) return;
                BlockEntity entity = level.getBlockEntity(pos);
                CSProperties properties = CSProperties.of(wand);
                
                ((CSBooleanProperty) properties.properties().get(CSProperties.FLUIDLOGGABLE)).set(state.getValue(CrystalixGlass.FLUIDLOGGABLE));
                ((CSBooleanProperty) properties.properties().get(CSProperties.SHADELESS)).set(state.getValue(CrystalixGlass.SHADELESS));
                ((CSBooleanProperty) properties.properties().get(CSProperties.INVISIBLE)).set(state.getValue(CrystalixGlass.INVISIBLE));
                ((CSBooleanProperty) properties.properties().get(CSProperties.CLEAR)).set(state.getValue(CrystalixGlass.TRANSPARENT));
                ((CSEnumProperty<CSProperties.Light>) properties.properties().get(CSProperties.LIGHT)).set(state.getValue(CrystalixGlass.LIGHT));
                ((CSEnumProperty<CSProperties.Ghost>) properties.properties().get(CSProperties.GHOST)).set(state.getValue(CrystalixGlass.GHOST));
                
                if (entity instanceof CrystalixGlassTile tile) {
                    ((CSBooleanProperty) properties.properties().get(CSProperties.CONDUCTOR)).set(tile.isConductor());
                    ((CSBooleanProperty) properties.properties().get(CSProperties.REINFORCED)).set(tile.isReinforced());
                    ((CSIntegerProperty) properties.properties().get(CSProperties.COLOR)).set(tile.getColor());
                }
                
                player.getInventory().setChanged();
            }
        }).exceptionally(e -> {
            SwapPropertiesPayload.log.error("Failed to pick wand properties from BlockState", e);
            return null;
        });
    }
}
