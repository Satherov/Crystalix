package com.satherov.crystalix;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Crystalix.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CrystalixConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue MAX_WAND_EDIT = BUILDER
            .comment("Defines the maximum number of blocks that can be edited with the wand at once")
            .defineInRange("max_wand_edit", 512, 1, 16384);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int max_wand_edit;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        max_wand_edit = MAX_WAND_EDIT.get();
    }
}
