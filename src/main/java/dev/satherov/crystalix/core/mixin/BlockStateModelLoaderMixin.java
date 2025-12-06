package dev.satherov.crystalix.core.mixin;


import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mixin(BlockStateModelLoader.class)
public abstract class BlockStateModelLoaderMixin {
    
    @Unique
    private static Set<ResourceLocation> crystalix$deprecated;
    
    @Unique
    private static Set<ResourceLocation> crystalix$getDeprecated() {
        if (BlockStateModelLoaderMixin.crystalix$deprecated == null) {
            BlockStateModelLoaderMixin.crystalix$deprecated = CSRegistry.OLD_ENTRIES.values().stream()
                    .filter(Objects::nonNull).map(DeferredHolder::getId).collect(Collectors.toSet());
        }
        return BlockStateModelLoaderMixin.crystalix$deprecated;
    }
    
    @Inject(
            method = "loadBlockStateDefinitions(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/world/level/block/state/StateDefinition;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void crystalix$skip(ResourceLocation id, StateDefinition<Block, BlockState> def, CallbackInfo ci) {
        if (BlockStateModelLoaderMixin.crystalix$getDeprecated().contains(id)) {
            ci.cancel();
        }
    }
}


