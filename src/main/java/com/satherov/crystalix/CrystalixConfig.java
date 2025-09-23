package com.satherov.crystalix;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CrystalixConfig {
    
    static class Common {
        
        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
        
        private static final ModConfigSpec.IntValue MAX_EDIT_OPERATION = BUILDER
                .comment("Defines the maximum number of blocks that can be edited with the wand per tick")
                .defineInRange("max_edit_operation", 512, 1, Integer.MAX_VALUE);
        
        private static final ModConfigSpec.IntValue MAX_EDIT_FORCE = BUILDER
                .comment("Caps the maximum number of blocks that can be edited with the wand overall")
                .defineInRange("max_edit_force", 16384, 1, Integer.MAX_VALUE);
        
        public static final ModConfigSpec SPEC = BUILDER.build();
    }
    
    static class Client {
        
        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
        
        private static final ModConfigSpec.EnumValue<EJadeMode> JADE_MODE = BUILDER
                .comment("Defines how the jade info should be shown")
                .comment("ALWAYS - Always show Jade when looking at a block")
                .comment("WAND - Only show Jade when holding a wand")
                .comment("NEVER - Never show Jade")
                .defineEnum("jade_mode", EJadeMode.ALWAYS);
        
        public static final ModConfigSpec SPEC = BUILDER.build();
    }
    
    public static int getMaxEditOperation() {
        return Common.MAX_EDIT_OPERATION.get();
    }
    
    public static int getMaxEditForce() {
        return Common.MAX_EDIT_FORCE.get();
    }
    
    public static EJadeMode getJadeMode() {
        return Client.JADE_MODE.get();
    }
    
    public enum EJadeMode {
        ALWAYS,
        WAND,
        NEVER
    }
}
