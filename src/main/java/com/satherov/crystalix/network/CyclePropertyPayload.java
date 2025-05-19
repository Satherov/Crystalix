package com.satherov.crystalix.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.network.NetworkEvent;

import com.satherov.crystalix.content.CrystalixUtil;
import com.satherov.crystalix.content.item.CrystalixWand;
import com.satherov.crystalix.content.properties.BlockProperties;
import com.satherov.crystalix.content.properties.IProperty;

import java.util.function.Supplier;

public class CyclePropertyPayload {
    public final String key;
    public final String value;

    public CyclePropertyPayload(String key, String value) {
        this.key = key;
        this.value = value;
    }

    // write to the buffer
    public static void encode(CyclePropertyPayload msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.key);
        buf.writeUtf(msg.value);
    }

    // read from the buffer
    public static CyclePropertyPayload decode(FriendlyByteBuf buf) {
        String key = buf.readUtf(100);
        String value = buf.readUtf(100);
        return new CyclePropertyPayload(key, value);
    }

    // server‐side handler
    public static class Handler {
        public static void handle(CyclePropertyPayload msg, Supplier<NetworkEvent.Context> ctxSupplier) {
            NetworkEvent.Context ctx = ctxSupplier.get();
            ctx.enqueueWork(() -> {
                ServerPlayer player = ctx.getSender();
                if (player == null) return;
                ItemStack wand = CrystalixUtil.getWand(player);
                if (wand.isEmpty()) return;

                BlockProperties props = new BlockProperties(wand);
                IProperty<?> property = props.get(msg.key);
                if (property == null) return;

                property.setValueString(msg.value);
                CrystalixWand.sendMessage(player, property);
                player.getInventory().setChanged();
            });
            ctx.setPacketHandled(true);
        }
    }
}
