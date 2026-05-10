package dev.satherov.crystalix.compat.jei;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.core.registry.CXRegistry;
import dev.satherov.sathlib.core.annotations.NothingNull;

import net.minecraft.resources.Identifier;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IIngredientAliasRegistration;

@JeiPlugin
@NothingNull
public class CXJEICompat implements IModPlugin {
    
    public static final Identifier ID = Crystalix.id("crystalix");
    
    @Override
    public Identifier getPluginUid() {
        return CXJEICompat.ID;
    }
    
    @Override
    public void registerIngredientAliases(IIngredientAliasRegistration registration) {
        registration.addAlias(CXRegistry.CRYSTALIX_ITEM.get().getDefaultInstance(), "Witherproof Glass");
        registration.addAlias(CXRegistry.CRYSTALIX_ITEM.get().getDefaultInstance(), "Reinforced Glass");
        registration.addAlias(CXRegistry.CRYSTALIX_ITEM.get().getDefaultInstance(), "Glowing Glass");
        registration.addAlias(CXRegistry.CRYSTALIX_ITEM.get().getDefaultInstance(), "Ghost Glass");
        registration.addAlias(CXRegistry.CRYSTALIX_ITEM.get().getDefaultInstance(), "Ethereal Glass");
        registration.addAlias(CXRegistry.CRYSTALIX_ITEM.get().getDefaultInstance(), "Redstone Glass");
        registration.addAlias(CXRegistry.CRYSTALIX_ITEM.get().getDefaultInstance(), "Colored Glass");
    }
}
