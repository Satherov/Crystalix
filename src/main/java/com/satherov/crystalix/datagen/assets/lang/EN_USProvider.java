package com.satherov.crystalix.datagen.assets.lang;

import com.satherov.crystalix.Crystalix;
import com.satherov.crystalix.content.CrystalixRegistry;
import com.satherov.crystalix.datagen.assets.CrystalixLanguageProvider;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;

public class EN_USProvider extends CrystalixLanguageProvider {

    public EN_USProvider(PackOutput output) {
        super(output, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup." + Crystalix.MOD_ID, "Crystalix");

        add("crystalix.configuration.max_wand_edit", "Maxiumum Wand Edit Amount");

        add("crystalix.networking.cycle_property.failed", "Error sending Crystalix property change to server:");

        add("key.crystalix.category", "Crystalix");
        add("key.crystalix.cycle_shadeless", "Cycle Shadeless Mode");
        add("key.crystalix.cycle_reinforced", "Cycle Reinforced Mode");
        add("key.crystalix.cycle_light", "Cycle Light Mode");
        add("key.crystalix.cycle_ghost", "Cycle Ghost Mode");

        add("crystalix.wand.bulk", "§7Hold Shift to Bulk Edit");

        add("crystalix.property.shadeless", "Shadeless Mode");
        add("crystalix.property.shadeless.enabled", "Enabled");
        add("crystalix.property.shadeless.disabled", "Disabled");

        add("crystalix.property.reinforced", "Reinforced Mode");
        add("crystalix.property.reinforced.enabled", "Enabled");
        add("crystalix.property.reinforced.disabled", "Disabled");

        add("crystalix.property.light", "Light Mode");
        add("crystalix.property.light.light", "§6Light");
        add("crystalix.property.light.dark", "§8Dark");
        add("crystalix.property.light.none", "§7None");

        add("crystalix.property.ghost", "Ghost Mode");
        add("crystalix.property.ghost.block_all", "§4Block §8All");
        add("crystalix.property.ghost.allow_all", "§2Allow §8All");
        add("crystalix.property.ghost.block_player", "§4Block §bPlayer");
        add("crystalix.property.ghost.allow_player", "§2Allow §bPlayer");
        add("crystalix.property.ghost.block_monster", "§4Block §cMonster");
        add("crystalix.property.ghost.allow_monster", "§2Allow §cMonster");
        add("crystalix.property.ghost.block_animal", "§4Block §aAnimal");
        add("crystalix.property.ghost.allow_animal", "§2Allow §aAnimal");

        CrystalixRegistry.BLOCKS.getEntries().forEach(block -> add(block.get(), format(block.getId().getPath())));
        CrystalixRegistry.ITEMS.getEntries().stream().filter(item -> !(item.get() instanceof BlockItem)).forEach(item -> add(item.get(), format(item.getId().getPath())));
    }
}
