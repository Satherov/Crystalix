package com.satherov.crystalix;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = Crystalix.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CrystalixConfig {

    public static final ForgeConfigSpec SPEC;
    public static final CommonConfig COMMON_CONFIG;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        SPEC = specPair.getRight();
        COMMON_CONFIG = specPair.getLeft();
    }

    public static class CommonConfig {

        public final ForgeConfigSpec.IntValue maxWandEdit;

        public CommonConfig(ForgeConfigSpec.Builder builder) {

            maxWandEdit = builder.comment("Defines the maximum number of blocks that can be edited with the wand at once")
                    .defineInRange("max_wand_edit", 512, 1, 16384);

        }
    }
}
