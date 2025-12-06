package dev.satherov.crystalix.data.providers.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class CSLootTableProvider extends LootTableProvider {
    
    public CSLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, UnaryOperator<ProviderList> providers) {
        super(output, Collections.emptySet(), providers.apply(new ProviderList()).entries(), registries);
    }
    
    public static class ProviderList {
        private final List<SubProviderEntry> providers = new ArrayList<>();
        
        public ProviderList add(Function<HolderLookup.Provider, LootTableSubProvider> provider, LootContextParamSet paramSet) {
            SubProviderEntry entry = new SubProviderEntry(provider, paramSet);
            this.providers.add(entry);
            return this;
        }
        
        public List<SubProviderEntry> entries() {
            return this.providers;
        }
    }
}
