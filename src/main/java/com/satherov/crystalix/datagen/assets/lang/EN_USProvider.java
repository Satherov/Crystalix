package com.satherov.crystalix.datagen.assets.lang;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.CrystalixRegistry;
import com.satherov.crystalix.datagen.assets.CrystalixLanguageProvider;

import net.minecraft.data.PackOutput;

public class EN_USProvider extends CrystalixLanguageProvider {

    public EN_USProvider(PackOutput output) {
        super(output, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + Crystalix.MOD_ID, "Crystalix");
        add(Crystalix.MOD_ID + ".tooltip.shadeless.enabled", "§2Does not have a shade effect");
        add(Crystalix.MOD_ID + ".tooltip.shadeless.disabled", "§4Has a shade effect");
        add(Crystalix.MOD_ID + ".tooltip.reinforced.enabled", "§2Is wither proof");
        add(Crystalix.MOD_ID + ".tooltip.reinforced.disabled", "§4Is not wither proof");
        add(Crystalix.MOD_ID + ".tooltip.light.enabled", "§2Does emit light");
        add(Crystalix.MOD_ID + ".tooltip.light.disabled", "§4Does not emit light");
        add(Crystalix.MOD_ID + ".tooltip.dark.enabled", "§2Blocks light");
        add(Crystalix.MOD_ID + ".tooltip.dark.disabled", "§4Does not block light");
        add(Crystalix.MOD_ID + ".tooltip.ghost.enabled", "§2Is passthrough");
        add(Crystalix.MOD_ID + ".tooltip.ghost.disabled", "§4Is not passthrough");
        CrystalixRegistry.BLOCKS.getEntries().forEach(block -> add(block.get(), format(block.getId().getPath())));
    }
}
