package dev.satherov.crystalix.config;

import dev.satherov.crystalix.config.annotation.Config;
import dev.satherov.crystalix.config.annotation.ConfigVal;

import net.neoforged.fml.config.ModConfig;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Config(ModConfig.Type.CLIENT)
public class CSClientConfig {
    
    @ConfigVal(name = "jade_mode", comment = "Defines when Jade should be displayed")
    @ConfigVal.Enum(Jade.class)
    private static @Getter Jade JadeMode = Jade.ALWAYS;
    
    @ConfigVal(name = "wand_info", comment = "Defines whether wand information should be displayed and where")
    @ConfigVal.Enum(AnchorPosition.class)
    private static @Getter AnchorPosition WandInfo = AnchorPosition.TOP_RIGHT;
    
    @RequiredArgsConstructor
    @Accessors(fluent = true)
    public enum Jade implements ConfigEnum {
        ALWAYS("Always show Jade when looking at a block"),
        WAND("Only show Jade when holding a wand"),
        NEVER("Never show Jade");
        
        private final @Getter String comment;
    }
    
    @RequiredArgsConstructor
    @Accessors(fluent = true)
    public enum AnchorPosition implements ConfigEnum {
        NONE("Do not display overlay"),
        TOP_LEFT("Anchor overlay to top-left corner"),
        TOP_RIGHT("Anchor overlay to top-right corner");
        
        private final @Getter String comment;
    }
}
