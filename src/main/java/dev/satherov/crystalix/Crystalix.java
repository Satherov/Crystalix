package dev.satherov.crystalix;

import dev.satherov.crystalix.compat.framedblocks.CXFramedBlocksCompat;
import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.crystalix.network.CyclePropertyPayload;
import dev.satherov.crystalix.network.SetColorPayload;
import dev.satherov.crystalix.network.SetGlassMaterialPayload;
import dev.satherov.crystalix.network.SwapPropertiesPayload;
import dev.satherov.sathlib.compat.Mods;
import dev.satherov.sathlib.config.SLConfigLoader;
import dev.satherov.sathlib.network.handling.SLNetworkManager;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.javafmlmod.FMLModContainer;

import net.minecraft.resources.Identifier;

@Mod(Crystalix.MOD_ID)
public class Crystalix {
    
    public static final String MOD_ID = "crystalix";
    
    public static final SLNetworkManager NETWORK = SLNetworkManager.create(Crystalix.MOD_ID);
    
    public Crystalix(final IEventBus bus, final FMLModContainer container) {
        SLConfigLoader.discover(container);
        CXRegistry.register(bus);
        Crystalix.NETWORK.add(new CyclePropertyPayload.Provider());
        Crystalix.NETWORK.add(new SetColorPayload.Provider());
        Crystalix.NETWORK.add(new SetGlassMaterialPayload.Provider());
        Crystalix.NETWORK.add(new SwapPropertiesPayload.Provider());
        Crystalix.NETWORK.register(bus);
        
        
        Mods.FRAMED_BLOCKS.run(() -> CXFramedBlocksCompat.register(bus));
    }
    
    public static Identifier id(final String path) {
        return Identifier.fromNamespaceAndPath(Crystalix.MOD_ID, path);
    }
}
