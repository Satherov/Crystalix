package dev.satherov.crystalix.compat.framedblocks;

import dev.satherov.crystalix.Crystalix;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import xfacthd.framedblocks.api.camo.CamoContainerFactory;
import xfacthd.framedblocks.api.util.FramedConstants;

@Mod(value = Crystalix.MOD_ID)
public final class CSFramedBlocksCompat {
    
    public CSFramedBlocksCompat(IEventBus modBus) {
        if (ModList.get().isLoaded("framedblocks")) {
            Guarded.init(modBus);
        }
    }
    
    static final class Guarded {
        
        private static final DeferredRegister<CamoContainerFactory<?>> CAMO_FACTORIES = DeferredRegister.create(FramedConstants.CAMO_CONTAINER_FACTORY_REGISTRY_KEY, Crystalix.MOD_ID);
        
        static final DeferredHolder<CamoContainerFactory<?>, CrystalixGlassCamoContainerFactory> FACTORY_CS_BLOCK = Guarded.CAMO_FACTORIES.register(
                "crystalix_glass", CrystalixGlassCamoContainerFactory::new
        );
        
        private static void init(IEventBus modBus) {
            Guarded.CAMO_FACTORIES.register(modBus);
        }
        
        private Guarded() { }
    }
}
