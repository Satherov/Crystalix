package dev.satherov.crystalix.compat;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;

import java.util.Objects;

@JeiPlugin
public class CSJEIPlugin implements IModPlugin {
    
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return Crystalix.rl("jei_plugin");
    }
    
    public void registerRecipes(IRecipeRegistration registration) {
        registration.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, CSRegistry.OLD_ENTRIES.values().stream()
                .filter(Objects::nonNull)
                .map(DeferredHolder::get)
                .map(ItemStack::new)
                .toList()
        );
    }
}
