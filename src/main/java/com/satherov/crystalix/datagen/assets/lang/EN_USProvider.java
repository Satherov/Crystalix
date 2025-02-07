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

        add("key.crystalix.category", "Crystalix");
        add("key.crystalix.cycle_shadeless", "Cycle Shadeless Mode");
        add("key.crystalix.cycle_reinforced", "Cycle Reinforced Mode");
        add("key.crystalix.cycle_light", "Cycle Light Mode");
        add("key.crystalix.cycle_ghost", "Cycle Ghost Mode");

        add("tooltip.crystalix.wand.bulk", "§7Hold Shift to Bulk Edit");
        add("tooltip.crystalix.wand.shadeless.enabled", "§7Shadeless Mode: §2Enabled");
        add("tooltip.crystalix.wand.shadeless.disabled", "§7Shadeless Mode: §4Disabled");
        add("tooltip.crystalix.wand.reinforced.enabled", "§7Reinforced Mode: §2Enabled");
        add("tooltip.crystalix.wand.reinforced.disabled", "§7Reinforced Mode: §4Disabled");
        add("tooltip.crystalix.wand.light.light", "§7Light Mode: §6Light");
        add("tooltip.crystalix.wand.light.dark", "§7Light Mode: §8Dark");
        add("tooltip.crystalix.wand.light.none", "§7Light Mode: §7None");
        add("tooltip.crystalix.wand.ghost.block_all", "§7Ghost Mode: §4Block §8All");
        add("tooltip.crystalix.wand.ghost.allow_all", "§7Ghost Mode: §2Allow §8All");
        add("tooltip.crystalix.wand.ghost.block_player", "§7Ghost Mode: §4Block §bPlayer");
        add("tooltip.crystalix.wand.ghost.allow_player", "§7Ghost Mode: §2Allow §bPlayer");
        add("tooltip.crystalix.wand.ghost.block_monster", "§7Ghost Mode: §4Block §cMonster");
        add("tooltip.crystalix.wand.ghost.allow_monster", "§7Ghost Mode: §2Allow §cMonster");
        add("tooltip.crystalix.wand.ghost.block_animal", "§7Ghost Mode: §4Block §aAnimal");
        add("tooltip.crystalix.wand.ghost.allow_animal", "§7Ghost Mode: §2Allow §aAnimal");

        CrystalixRegistry.BLOCKS.getEntries().forEach(block -> add(block.get(), format(block.getId().getPath())));
        CrystalixRegistry.ITEMS.getEntries().stream().filter(item -> !(item.get() instanceof BlockItem)).forEach(item -> add(item.get(), format(item.getId().getPath())));
    }
}
