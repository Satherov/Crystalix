package dev.satherov.crystalix.compat.framedblocks;

import dev.satherov.crystalix.Crystalix;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import io.github.xfacthd.framedblocks.api.camo.CamoContainerFactory;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;

public class CXFramedBlocksCompat {
    
    private static final DeferredRegister<CamoContainerFactory<?>> CAMO_FACTORIES = DeferredRegister.create(FramedConstants.Registries.CAMO_CONTAINER_FACTORY_REGISTRY_KEY, Crystalix.MOD_ID);
    
    protected static final DeferredHolder<CamoContainerFactory<?>, CrystalixGlassCamoContainerFactory> CRYSTALIX_GLASS_CAMO_FACTORY = CXFramedBlocksCompat.CAMO_FACTORIES.register(
            "crystalix_glass", CrystalixGlassCamoContainerFactory::new
    );
    
    public static void register(IEventBus modBus) {
        CXFramedBlocksCompat.CAMO_FACTORIES.register(modBus);
    }
}
