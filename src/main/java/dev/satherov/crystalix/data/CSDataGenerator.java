package dev.satherov.crystalix.data;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.data.providers.CSBlockModelProvider;
import dev.satherov.crystalix.data.providers.CSBlockStateProvider;
import dev.satherov.crystalix.data.providers.CSFusionModelProvider;
import dev.satherov.crystalix.data.providers.CSItemModelProvider;
import dev.satherov.crystalix.data.providers.CSLocaleProvider;
import dev.satherov.crystalix.data.providers.CSRecipeProvider;
import dev.satherov.crystalix.data.providers.loot.CSBlockLootProvider;
import dev.satherov.crystalix.data.providers.loot.CSLootTableProvider;
import dev.satherov.crystalix.data.providers.tags.CSBlockTagProvider;
import dev.satherov.crystalix.data.providers.tags.CSItemTagProvider;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

@EventBusSubscriber(modid = Crystalix.MOD_ID)
public class CSDataGenerator {
    
    @SubscribeEvent
    private static void onGatherData(GatherDataEvent event) {
        CSDataProvider provider = CSDataProvider.create(event);
        
        provider.add(event.includeClient(), (gen, out, helper, lookup) -> new CSLocaleProvider(out));
        provider.add(event.includeClient(), (gen, out, helper, lookup) -> new CSBlockModelProvider(out, helper));
        provider.add(event.includeClient(), (gen, out, helper, lookup) -> new CSFusionModelProvider(out, helper));
        provider.add(event.includeClient(), (gen, out, helper, lookup) -> new CSBlockStateProvider(out, helper));
        provider.add(event.includeClient(), (gen, out, helper, lookup) -> new CSItemModelProvider(out, helper));
        
        provider.add(event.includeServer(), (gen, out, helper, lookup) -> new CSItemTagProvider(out, lookup));
        provider.add(event.includeServer(), (gen, out, helper, lookup) -> new CSBlockTagProvider(out, lookup, helper));
        provider.add(event.includeServer(), (gen, out, helper, lookup) -> new CSRecipeProvider(out, lookup));
        provider.add(event.includeServer(), (gen, out, helper, lookup) -> new CSLootTableProvider(out, lookup, e -> e.add(CSBlockLootProvider::new, LootContextParamSets.BLOCK)));
        
        provider.generate();
    }
}

