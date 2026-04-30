package dev.satherov.crystalix;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.UtilityClass;

import dev.satherov.sathlib.config.data.Config;
import dev.satherov.sathlib.config.data.ConfigEntry;
import dev.satherov.sathlib.config.data.ConfigEnum;
import dev.satherov.sathlib.config.data.ConfigHolder;

import net.neoforged.fml.config.ModConfig;

@UtilityClass
@ConfigHolder
public class CXConfig {
    
    @Config(ModConfig.Type.COMMON)
    public static final class Common {
        
        @Getter
        @ConfigEntry(comment = """
                After how many the multi-block edit mode of the wand should stop.
                This has no effect on performance, but you might want to limit it anyways.
                """)
        private static int maxBlockEdits = 262_144;
        
        @Getter
        @ConfigEntry(comment = """
                The maximum number of blocks that can be modified in a single tick by multi-block edit.
                If you're experiencing performance issues you might want to lower this number.
                """)
        private static int maxBlockEditsPerTick = 256;
    }
    
    @Config(ModConfig.Type.CLIENT)
    public static final class Client {
        
        @Getter
        @ConfigEntry(comment = "Decides when Jade should be displayed")
        private static Jade JadeMode = Jade.WAND;
        
        @RequiredArgsConstructor
        @Accessors(fluent = true)
        public enum Jade implements ConfigEnum {
            // @formatter:off
            ALWAYS("Always show Jade"),
            WAND  ("Only show Jade while holding a wand"),
            NEVER ("Never show Jade"),
            // @formatter:off
            ;
            
            private final @Getter String description;
        }
    }
}
