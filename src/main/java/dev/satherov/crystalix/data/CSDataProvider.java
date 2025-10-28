package dev.satherov.crystalix.data;


import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.core.annotations.NothingNull;

import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@NothingNull
public class CSDataProvider implements DataProvider {
    
    private final boolean server;
    private final DataGenerator generator;
    private final ExistingFileHelper fileHelper;
    private final PackOutput output;
    private final CompletableFuture<HolderLookup.Provider> lookup;
    private final RegistrySetBuilder builder;
    private final List<Provider> providers = new ArrayList<>();
    
    private CSDataProvider(GatherDataEvent event) {
        this.generator = event.getGenerator();
        this.fileHelper = event.getExistingFileHelper();
        this.output = generator.getPackOutput();
        this.lookup = event.getLookupProvider();
        this.builder = new RegistrySetBuilder();
        this.server = event.includeServer();
    }
    
    public static CSDataProvider create(GatherDataEvent event) {
        return new CSDataProvider(event);
    }
    
    public void add(boolean include, Provider provider) {
        if (include) providers.add(provider);
    }
    
    public <T> void add(ResourceKey<? extends Registry<T>> key, RegistrySetBuilder.RegistryBootstrap<T> bootstrap) {
        builder.add(key, bootstrap);
    }
    
    public void generate() {
        this.generator.addProvider(true, this);
        this.generator.addProvider(server, (Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(output, lookup, builder, Set.of(Crystalix.MOD_ID)));
    }
    
    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        List<CompletableFuture<?>> list = new ArrayList<>();
        for (Provider provider : providers) {
            list.add(provider.create(generator, output, fileHelper, lookup).run(cachedOutput));
        }
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }
    
    @Override
    public String getName() {
        return "Crystalix Data Provider";
    }
    
    @FunctionalInterface
    public interface Provider {
        DataProvider create(DataGenerator generator, PackOutput output, ExistingFileHelper fileHelper, CompletableFuture<HolderLookup.Provider> lookup);
    }
}
