package dev.satherov.crystalix.compat.jade;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.common.block.CrystalixGlassBlock;
import dev.satherov.sathlib.core.annotations.NothingNull;

import net.minecraft.resources.Identifier;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@NothingNull
@WailaPlugin
public class CXJadePlugin implements IWailaPlugin {
    
    public static final Identifier ID = Crystalix.id("crystalix");
    
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(CXJadeProvider.INSTANCE, CrystalixGlassBlock.class);
    }
}
