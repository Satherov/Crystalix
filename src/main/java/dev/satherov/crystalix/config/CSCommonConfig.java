package dev.satherov.crystalix.config;

import dev.satherov.crystalix.config.annotation.Config;
import dev.satherov.crystalix.config.annotation.ConfigVal;

import net.neoforged.fml.config.ModConfig;

import lombok.Getter;

@Config(ModConfig.Type.COMMON)
public class CSCommonConfig {
    
    @ConfigVal(name = "max_edit_operations", comment = "Defines the maximum number of blocks that can be edited with the wand per tick")
    @ConfigVal.Integer(min = 1)
    private static final @Getter int MaxEditOperations = 512;
    
    @ConfigVal(name = "max_edit_force", comment = "Caps the maximum number of blocks that can be edited with the wand overall")
    @ConfigVal.Integer(min = 1)
    private static final @Getter int MaxEditForce = 16384;
}
