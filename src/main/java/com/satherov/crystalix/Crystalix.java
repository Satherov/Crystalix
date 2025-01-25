package com.satherov.crystalix;

import com.satherov.crystalix.content.CrystalixRegistry;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(Crystalix.MOD_ID)
public class Crystalix {
    public static final String MOD_ID = "crystalix";

    public Crystalix(IEventBus modEventBus, ModContainer modContainer) {
        CrystalixRegistry.BLOCKS.register(modEventBus);
        CrystalixRegistry.ITEMS.register(modEventBus);
        CrystalixRegistry.CREATIVE_TABS.register(modEventBus);
    }
}
