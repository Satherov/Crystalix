package dev.satherov.crystalix.data;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.data.provider.CXLanguageProvider;
import dev.satherov.crystalix.data.provider.CXModelProvider;
import dev.satherov.crystalix.data.provider.CXRecipeProvider;
import dev.satherov.crystalix.data.provider.loot.CXLootProvider;
import dev.satherov.crystalix.data.provider.tags.CXBlockTagsProvider;
import dev.satherov.crystalix.data.provider.tags.CXItemTagsProvider;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Crystalix.MOD_ID)
public class CXDataGenerator {
    
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        if (!Crystalix.MOD_ID.equalsIgnoreCase(event.getModContainer().getModId())) return;
        
        event.createProvider(CXLanguageProvider::new);
        event.createProvider(CXModelProvider::new);
        
        event.createProvider(CXBlockTagsProvider::new);
        event.createProvider(CXItemTagsProvider::new);
        event.createProvider(CXLootProvider::create);
        event.createProvider(CXRecipeProvider.Runner::new);
    }
}
