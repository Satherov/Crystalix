package com.satherov.crystalix.datagen.assets;

import net.minecraft.data.PackOutput;

import net.minecraftforge.common.data.LanguageProvider;

import com.satherov.crystalix.Crystalix;

import java.util.Arrays;
import java.util.stream.Collectors;

public abstract class CrystalixLanguageProvider extends LanguageProvider {

    public CrystalixLanguageProvider(PackOutput output, String locale) {
        super(output, Crystalix.MOD_ID, locale);
    }

    protected String format(String snake) {
        return Arrays.stream(snake.split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }
}
