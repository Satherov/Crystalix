package com.satherov.crystalix.datagen.assets;

import com.satherov.crystalix.Crystalix;

import net.neoforged.neoforge.common.data.LanguageProvider;

import net.minecraft.data.PackOutput;

public class CrystalixLanguageProvider extends LanguageProvider {

    public CrystalixLanguageProvider(PackOutput output, String locale) {
        super(output, Crystalix.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {
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
