package com.rinko1231.despawnnotifier.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class DespawnNotifierConfig {

    public static final ModConfigSpec SPEC;
    public static ModConfigSpec.IntValue itemFlashStartTimePercentage;
    public static ModConfigSpec.BooleanValue isUrgentFlashEnabled;
    public static ModConfigSpec.IntValue urgentFlashThresholdPercentage;
    public static ModConfigSpec.DoubleValue flashAlphaMin;
    public static ModConfigSpec.DoubleValue flashAlphaMax;
    public static ModConfigSpec.IntValue flashCycleTicks;

    static
    {
        ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
        BUILDER.translation("config.despawnnotifier.config").push("Despawn Notifier Config");

        itemFlashStartTimePercentage = BUILDER
                .translation("config.despawnnotifier.item_flash_start_time_percentage")
                .comment("Flashing will start when the remaining total duration percentage is reached")
                .defineInRange("itemFlashStartTimePercentage", 20,0,100);
        isUrgentFlashEnabled =BUILDER
                .translation("config.despawnnotifier.is_urgent_flash_enabled")
                .comment("Set to true to have item flash faster as it gets closer to despawning")
                .define("isUrgentFlashEnabled",true);
        urgentFlashThresholdPercentage = BUILDER
                .translation("config.despawnnotifier.urgent_flash_threshold_percentage")
                .comment("When remaining time is below this percentage, urgent flashing will activate (if enabled)")
                .defineInRange("urgentFlashThresholdPercentage", 5, 0, 100);
        flashAlphaMin = BUILDER
                .translation("config.despawnnotifier.flash_alpha_min")
                .comment("Minimum alpha (transparency) when flashing. 0.0 = fully transparent, 1.0 = opaque")
                .defineInRange("flashAlphaMin", 0.2, 0.0, 1.0);
        flashAlphaMax = BUILDER
                .translation("config.despawnnotifier.flash_alpha_max")
                .comment("Maximum alpha (transparency) when flashing")
                .defineInRange("flashAlphaMax", 1.0, 0.0, 1.0);
        flashCycleTicks = BUILDER
                .translation("config.despawnnotifier.flash_cycle_ticks")
                .comment("Tick duration of one full flash cycle")
                .defineInRange("flashCycleTicks", 20, 0, Integer.MAX_VALUE);

        SPEC = BUILDER.build();
    }
}
