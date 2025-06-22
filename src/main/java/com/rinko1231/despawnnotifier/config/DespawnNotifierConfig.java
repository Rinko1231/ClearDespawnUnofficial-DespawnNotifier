package com.rinko1231.despawnnotifier.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class DespawnNotifierConfig {

    public static final ModConfigSpec SPEC;
    public static ModConfigSpec.IntValue itemFlashStartTime;
    public static ModConfigSpec.BooleanValue isUrgentFlashEnabled;
    public static ModConfigSpec.BooleanValue useBreathingEffect;

    static
    {
        ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
        BUILDER.push("Despawn Notifier Config");

        itemFlashStartTime = BUILDER
                .defineInRange("Blinking start time before the item despawns, in seconds", 20, 1, 300);
        
        isUrgentFlashEnabled = BUILDER
                .define("Set to true to have item flash faster as it gets closer to despawning", true);
        
        useBreathingEffect = BUILDER
                .comment("Use breathing effect (fade in/out) instead of blinking")
                .define("Use breathing effect", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }


}
