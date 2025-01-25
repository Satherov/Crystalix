package com.satherov.crystalix.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;

public class CrystalixDataProvider implements DataProvider {

    private final List<DataProvider> subProviders = new ArrayList<>();

    public void addSubProvider(boolean include, DataProvider provider) {
        if (include) subProviders.add(provider);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        List<CompletableFuture<?>> list = new ArrayList<>();
        for (DataProvider provider : subProviders) list.add(provider.run(cachedOutput));
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Example Mod Data Provider";
    }
}
