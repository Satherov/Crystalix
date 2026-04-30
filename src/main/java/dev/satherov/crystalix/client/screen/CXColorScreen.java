package dev.satherov.crystalix.client.screen;

import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.crystalix.network.SetColorPayload;
import dev.satherov.sathlib.client.screen.ColorPickerScreen;

import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public class CXColorScreen extends ColorPickerScreen {
    
    private final CXRadialScreen parent;
    
    public CXColorScreen(CXRadialScreen parent, ItemStack stack) {
        super(stack.getOrDefault(CXRegistry.COLOR, 0xFFFFFF), value -> {
            stack.set(CXRegistry.COLOR, value);
            ClientPacketDistributor.sendToServer(new SetColorPayload(value));
        });
        this.parent = parent;
    }
    
    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }
}
