package com.satherov.crystalix.datagen.assets.lang;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;

import com.satherov.crystalix.content.CrystalixRegistry;
import com.satherov.crystalix.core.lang.CrystalixLanguage;
import com.satherov.crystalix.datagen.assets.CrystalixLanguageProvider;

public class EN_USProvider extends CrystalixLanguageProvider {

    public EN_USProvider(PackOutput output) {
        super(output, "en_us");
    }

    @Override
    protected void addTranslations() {

        add(CrystalixLanguage.ITEM_GROUP, "Crystalix");
        add(CrystalixLanguage.NETWORK_CYCLE_FAILED, "Error sending Crystalix property change to server: %s");

        add(CrystalixLanguage.CONFIG_MAX_EDIT, "Maximum Wand Edit Amount");
        add(CrystalixLanguage.CONFIG_JADE_MODE, "Jade Mode");
        add(CrystalixLanguage.CONFIG_JADE_CRYSTALIX_BLOCK, "Crystalix Block");

        add(CrystalixLanguage.TOOLTIP_BULK, "Hold Shift to Bulk Edit");

        add(CrystalixLanguage.KEY_CATEGORY, "Crystalix");
        add(CrystalixLanguage.KEY_DIRECTION_MODIFIER, "Direction Modifier");
        add(CrystalixLanguage.KEY_INVISIBLE, "Toggle Invisible Mode");
        add(CrystalixLanguage.KEY_SHADELESS, "Toggle Shadeless Mode");
        add(CrystalixLanguage.KEY_REINFORCED, "Toggle Reinforced Mode");
        add(CrystalixLanguage.KEY_WATERLOGGABLE, "Toggle Waterlogging Mode");
        add(CrystalixLanguage.KEY_LIGHT, "Cycle Light Mode");
        add(CrystalixLanguage.KEY_GHOST, "Cycle Ghost Mode");

        add(CrystalixLanguage.PROPERTY_INVISIBLE, "Invisible Mode");
        add(CrystalixLanguage.PROPERTY_SHADELESS, "Shadeless Mode");
        add(CrystalixLanguage.PROPERTY_REINFORCED, "Reinforced Mode");
        add(CrystalixLanguage.PROPERTY_WATERLOGGABLE, "Waterlogging Mode");
        add(CrystalixLanguage.PROPERTY_LIGHT, "Light Mode");
        add(CrystalixLanguage.PROPERTY_GHOST, "Ghost Mode");

        add(CrystalixLanguage.PROPERTY_ENABLED, "Enabled");
        add(CrystalixLanguage.PROPERTY_DISABLED, "Disabled");

        add(CrystalixLanguage.PROPERTY_LIGHT_NONE, "None");
        add(CrystalixLanguage.PROPERTY_LIGHT_DARK, "Dark");
        add(CrystalixLanguage.PROPERTY_LIGHT_LIGHT, "Light");
        add(CrystalixLanguage.PROPERTY_LIGHT_FAKE, "Fake Light");

        add(CrystalixLanguage.PROPERTY_GHOST_ALLOW, "Allow");
        add(CrystalixLanguage.PROPERTY_GHOST_DENY, "Deny");
        add(CrystalixLanguage.PROPERTY_GHOST_ALL, "All");
        add(CrystalixLanguage.PROPERTY_GHOST_PLAYER, "Player");
        add(CrystalixLanguage.PROPERTY_GHOST_ANIMAL, "Animal");
        add(CrystalixLanguage.PROPERTY_GHOST_MONSTER, "Monster");

        CrystalixRegistry.BLOCKS.getEntries().forEach(block -> add(block.get(), format(block.getId().getPath())));
        CrystalixRegistry.ITEMS.getEntries().stream().filter(item -> !(item.get() instanceof BlockItem)).forEach(item -> add(item.get(), format(item.getId().getPath())));
    }

    private void add(CrystalixLanguage lang, String translation) {
        this.add(lang.getTranslationKey(), translation);
    }
}
