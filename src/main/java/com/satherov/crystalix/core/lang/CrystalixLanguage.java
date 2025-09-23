package com.satherov.crystalix.core.lang;

import net.minecraft.Util;

import com.satherov.crystalix.Crystalix;

public enum CrystalixLanguage implements ILangEntry {
    ITEM_GROUP("itemGroup.crystalix"),
    
    NETWORK_CYCLE_FAILED("network", "cycle.failed"),
    
    MESSAGE_SIZE("message", "size"),
    
    TOOLTIP_BULK("tooltip", "bulk"),
    
    KEY_CATEGORY("key", "category"),
    KEY_DIRECTION_MODIFIER("key", "direction_modifier"),
    KEY_SHADELESS("key", "shadeless"),
    KEY_REINFORCED("key", "reinforce"),
    KEY_INVISIBLE("key", "invisible"),
    KEY_WATERLOGGABLE("key", "waterloggable"),
    KEY_LIGHT("key", "light"),
    KEY_GHOST("key", "ghost"),
    
    PROPERTY_INVISIBLE("property", "invisible"),
    PROPERTY_GHOST("property", "ghost"),
    PROPERTY_SHADELESS("property", "shadeless"),
    PROPERTY_REINFORCED("property", "reinforce"),
    PROPERTY_WATERLOGGABLE("property", "waterloggable"),
    PROPERTY_LIGHT("property", "light"),
    
    PROPERTY_ENABLED("property", "enabled"),
    PROPERTY_DISABLED("property", "disabled"),
    
    PROPERTY_LIGHT_NONE("property", "light.none"),
    PROPERTY_LIGHT_LIGHT("property", "light.light"),
    PROPERTY_LIGHT_DARK("property", "light.dark"),
    PROPERTY_LIGHT_FAKE("property", "light.fake"),
    
    PROPERTY_GHOST_ALLOW("property", "ghost.allow"),
    PROPERTY_GHOST_DENY("property", "ghost.deny"),
    PROPERTY_GHOST_ALL("property", "ghost.all"),
    PROPERTY_GHOST_PLAYER("property", "ghost.player"),
    PROPERTY_GHOST_ANIMAL("property", "ghost.animal"),
    PROPERTY_GHOST_MONSTER("property", "ghost.monster"),
    
    CONFIG_MAX_EDIT_OPERATION("crystalix.configuration.max_edit_operation"),
    CONFIG_MAX_EDIT_FORCE("crystalix.configuration.max_edit_force"),
    CONFIG_JADE_MODE("crystalix.configuration.jade_mode"),
    CONFIG_JADE_CRYSTALIX_BLOCK("config.jade.plugin_crystalix.crystalix_block");
    
    private final String key;
    
    CrystalixLanguage(String type, String key) {
        this(Util.makeDescriptionId(type, Crystalix.rl(key)));
    }
    
    CrystalixLanguage(String key) {
        this.key = key;
    }
    
    @Override
    public String getTranslationKey() {
        return key;
    }
}
