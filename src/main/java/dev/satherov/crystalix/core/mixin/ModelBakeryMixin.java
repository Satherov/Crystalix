package dev.satherov.crystalix.core.mixin;

import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    
    @Unique
    private static Set<ResourceLocation> crystalix$deprecated;
    
    @Unique
    private static Set<ResourceLocation> crystalix$getDeprecated() {
        if (crystalix$deprecated == null) {
            crystalix$deprecated = CSRegistry.OLD_ENTRIES.values().stream()
                    .filter(Objects::nonNull).map(DeferredHolder::getId).collect(Collectors.toSet());
        }
        return crystalix$deprecated;
    }
    
    @Inject(
            method = "loadItemModelAndDependencies(Lnet/minecraft/resources/ResourceLocation;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void crystalix$skipSomeItems(ResourceLocation id, CallbackInfo ci) {
        if (crystalix$getDeprecated().contains(id)) {
            ci.cancel();
        }
    }
}

