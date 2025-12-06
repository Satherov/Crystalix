package dev.satherov.crystalix.data.providers;

import dev.satherov.crystalix.Crystalix;
import dev.satherov.crystalix.client.lang.CSLanguage;
import dev.satherov.crystalix.core.CSRegistry;

import net.neoforged.neoforge.common.data.LanguageProvider;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;

public class CSLocaleProvider extends LanguageProvider {
    
    public CSLocaleProvider(PackOutput output) {
        super(output, Crystalix.MOD_ID, "en_us");
        
    }
    
    @Override
    protected void addTranslations() {
        CSLanguage.translate(this::add);
        CSRegistry.BLOCKS.getEntries().forEach(entry -> this.add(entry.get(), this.format(entry.getId().getPath())));
        CSRegistry.ITEMS.getEntries().stream().filter(item -> !(item.get() instanceof BlockItem)).forEach(entry -> this.add(entry.get(), this.format(entry.getId().getPath())));
    }
    
    protected String format(String string) {
        String[] words = string.replace("_", " ").split(" ");
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            formatted.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }
        return formatted.toString().trim();
    }
}
