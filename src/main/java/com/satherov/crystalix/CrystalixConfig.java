package com.satherov.crystalix;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class CrystalixConfig {

    static class Common {

        private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

        private static final ModConfigSpec.IntValue MAX_WAND_EDIT = BUILDER
                .comment("Defines the maximum number of blocks that can be edited with the wand at once")
                .defineInRange("max_wand_edit", 512, 1, Integer.MAX_VALUE);

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

    public static int getMaxWandEdit() {
        return Common.MAX_WAND_EDIT.get();
    }

    public static EJadeMode getJadeMode() {
        return Client.JADE_MODE.get();
    }

    public enum EJadeMode {
        ALWAYS,
        WAND,
        NEVER;
    }
}
