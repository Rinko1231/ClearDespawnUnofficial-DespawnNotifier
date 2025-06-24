package com.rinko1231.despawnnotifier.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.rinko1231.despawnnotifier.DespawnNotifier;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import org.jetbrains.annotations.NotNull;

import static com.rinko1231.despawnnotifier.config.DespawnNotifierConfig.*;

public class FlashItemEntityRenderer extends ItemEntityRenderer {
    public FlashItemEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull ItemEntity itemEntity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        int age = itemEntity.getAge();
        int lifespan = itemEntity.lifespan;

        FlashMultiBufferSource flashMultiBufferSource = new FlashMultiBufferSource(buffer, getAlpha(age, lifespan));
        super.render(itemEntity, entityYaw, partialTicks, poseStack, flashMultiBufferSource, packedLight);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();

        if (DespawnNotifier.isDev()) {
            this.renderNameTag(itemEntity, Component.literal("Age: %s".formatted(age)), poseStack, buffer, packedLight, partialTicks);
        }
    }

    private float getAlpha(int age, int lifespan) {
        int remaining = lifespan - age;
        int flashStartTicks = lifespan * itemFlashStartTimePercentage.get() / 100;
        int urgentThresholdTicks = lifespan * urgentFlashThresholdPercentage.get() / 100;

        if (age < (lifespan - flashStartTicks)) {
            return 1.0f;
        }

        int baseCycle = flashCycleTicks.get();
        int cycleTicks = baseCycle;

        if (isUrgentFlashEnabled.get() && remaining <= urgentThresholdTicks) {
            float percent = Math.max(0f, remaining / (float) urgentThresholdTicks);
            cycleTicks = Math.max(2, (int) (baseCycle * percent));
        }

        double alphaMin = flashAlphaMin.get();
        double alphaMax = flashAlphaMax.get();

        float phase = (age % cycleTicks) / (float) cycleTicks;
        float alphaFactor = (float) (0.5f - 0.5f * Math.cos(phase * 2 * Math.PI));
        return (float) (alphaMin + (alphaMax - alphaMin) * alphaFactor);
    }
}
