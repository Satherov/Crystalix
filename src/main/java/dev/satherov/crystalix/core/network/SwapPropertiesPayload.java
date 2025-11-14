package dev.satherov.crystalix.core.network;

import lombok.extern.slf4j.Slf4j;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.common.block.CrystalixGlass;
import dev.satherov.crystalix.common.item.CrystalixWand;
import dev.satherov.crystalix.common.properties.CSBooleanProperty;
import dev.satherov.crystalix.common.properties.CSEnumProperty;
import dev.satherov.crystalix.common.properties.CSProperties;
import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

@Slf4j
public record SwapPropertiesPayload(BlockState state) implements CustomPacketPayload {
    
    public static final StreamCodec<RegistryFriendlyByteBuf, SwapPropertiesPayload> STREAM_CODEC = CustomPacketPayload.codec(
            SwapPropertiesPayload::encode,
            SwapPropertiesPayload::new
    );
    
    public static final Type<SwapPropertiesPayload> TYPE = new Type<>(Crystalix.rl("swap_properties"));
    
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(Block.getId(state));
    }
    
    private SwapPropertiesPayload(RegistryFriendlyByteBuf buffer) {
        this(Block.stateById(buffer.readVarInt()));
    }
    
    @SuppressWarnings("unchecked")
    public static void handle(final SwapPropertiesPayload message, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.flow().isServerbound() && ctx.player() instanceof ServerPlayer player) {
                ItemStack wand = CrystalixWand.find(player);
                if (wand.isEmpty()) return;
                BlockState state = message.state();
                if (!(state.getBlock() instanceof CrystalixGlass glass)) return;
                CSProperties properties = CSProperties.of(wand);
                
                ((CSBooleanProperty) properties.properties().get(CSProperties.WATERLOGGABLE)).set(state.getValue(CrystalixGlass.WATERLOGGABLE));
                ((CSBooleanProperty) properties.properties().get(CSProperties.SHADELESS)).set(state.getValue(CrystalixGlass.SHADELESS));
                ((CSBooleanProperty) properties.properties().get(CSProperties.INVISIBLE)).set(state.getValue(CrystalixGlass.INVISIBLE));
                ((CSBooleanProperty) properties.properties().get(CSProperties.REINFORCED)).set(state.getValue(CrystalixGlass.REINFORCED));
                ((CSEnumProperty<CSProperties.Light>) properties.properties().get(CSProperties.LIGHT)).set(state.getValue(CrystalixGlass.LIGHT));
                ((CSEnumProperty<CSProperties.Ghost>) properties.properties().get(CSProperties.GHOST)).set(state.getValue(CrystalixGlass.GHOST));
                ((CSEnumProperty<CSRegistry.Colors>) properties.properties().get(CSProperties.COLOR)).set(glass.color());
                
                player.getInventory().setChanged();
            }
        }).exceptionally(e -> {
            log.error("Failed to pick wand properties from BlockState", e);
            return null;
        });
    }
}
