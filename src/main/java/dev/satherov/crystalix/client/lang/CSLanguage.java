package dev.satherov.crystalix.client.lang;

import dev.satherov.crystalix.Crystalix;

import net.minecraft.Util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.function.BiConsumer;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum CSLanguage implements CSTranslatable {
    CREATIVE_TAB_DEFAULT("itemGroup", "default", "Crystalix"),
    
    NETWORK_CYCLE_FAILED("network", "cycle.failed", "Error sending Crystalix property change to server: %s"),
    MESSAGE_SIZE("message", "size", "You seem to have a large selection of blocks. This may take a few seconds"),
    TOOLTIP_BULK("tooltip", "bulk", "Crouch to bulk edit"),
    
    PROPERTY_INVISIBLE("property", "invisible", "Invisible"),
    PROPERTY_WATERLOGGABLE("property", "waterloggable", "Waterloggable"),
    PROPERTY_SHADELESS("property", "shadeless", "Shadeless"),
    PROPERTY_REINFORCED("property", "reinforced", "Reinforced"),
    PROPERTY_GHOST("property", "ghost", "Ghost"),
    PROPERTY_LIGHT("property", "light", "Light"),
    PROPERTY_COLOR("property", "color", "Color"),
    
    PROPERTY_ENABLED("property", "enabled", "Enabled"),
    PROPERTY_DISABLED("property", "disabled", "Disabled"),
    PROPERTY_ALLOW("property", "allow", "Allow"),
    PROPERTY_DENY("property", "deny", "Deny"),
    
    PROPERTY_GHOST_ALL("property", "ghost.all", "All"),
    PROPERTY_GHOST_PLAYER("property", "ghost.player", "Player"),
    PROPERTY_GHOST_ANIMAL("property", "ghost.animal", "Animal"),
    PROPERTY_GHOST_MONSTER("property", "ghost.monster", "Monster"),
    
    PROPERTY_LIGHT_NONE("property", "light.none", "None"),
    PROPERTY_LIGHT_LIGHT("property", "light.light", "Light"),
    PROPERTY_LIGHT_DARK("property", "light.dark", "Dark"),
    PROPERTY_LIGHT_FAKE("property", "light.fake", "Fake Light"),
    
    KEY_CATEGORY("key", "category", "Crystalix"),
    KEY_WAND_CONFIG("key", "wand_config", "Open Wand Config"),
    KEY_COPY_PROPERTIES("key", "copy_properties", "Copy Properties"),
    
    CONFIG_MAX_EDIT_OPERATION("crystalix.configuration.max_edit_operations", "Per Tick Modification Limit"),
    CONFIG_MAX_EDIT_FORCE("crystalix.configuration.max_edit_force", "Modification Hard Limit"),
    CONFIG_JADE_MODE("crystalix.configuration.jade_mode", "Jade Mode"),
    CONFIG_WAND("crystalix.configuration.wand", "Wand"),
    CONFIG_WAND_INFO("crystalix.configuration.wand_info", "Wand Information"),
    
    CONFIG_JADE_CRYSTALIX_BLOCK("config.jade.plugin_crystalix.crystalix_block", "Crystalix Block");
    
    private final String key;
    private final String translation;
    
    CSLanguage(String type, String key, String translation) {
        this(Util.makeDescriptionId(type, Crystalix.rl(key)), translation);
    }
    
    public static void translate(BiConsumer<String, String> consumer) {
        for (CSLanguage lang : values()) {
            consumer.accept(lang.key(), lang.translation());
        }
    }
}
