package com.rinko1231.despawnnotifier.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rinko1231.despawnnotifier.DespawnNotifier;
import com.rinko1231.despawnnotifier.impl.IAlphaModifier;
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
        IAlphaModifier alphaModifier = (IAlphaModifier) this.itemRenderer;

        alphaModifier.mul(0.2f);
        super.render(itemEntity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        alphaModifier.mul(1.0f);

        if (DespawnNotifier.isDev()) {
            this.renderNameTag(itemEntity, Component.literal("Age: %s".formatted(age)), poseStack, buffer, packedLight, partialTicks);
        }
    }

    private float getAlpha(int age, int lifespan) {
        int remaining = lifespan - age;
        int flashStartTicks = lifespan * itemFlashStartTimePercentage.get() / 100;
        int urgentThresholdTicks = lifespan * urgentFlashThresholdPercentage.get() / 100;

        // 不在闪烁区间，alpha 为 1
        if (age < (lifespan - flashStartTicks)) {
            return 1.0f;
        }

        // 闪烁周期
        int baseCycle = flashCycleTicks.get();
        int cycleTicks = baseCycle;

        if (isUrgentFlashEnabled.get() && remaining <= urgentThresholdTicks) {
            // 越靠近 despawn，闪烁越快
            float percent = Math.max(0f, remaining / (float) urgentThresholdTicks); // [0,1]
            // 最快是 baseCycle 的一半（你可以改这个系数）
            cycleTicks = Math.max(2, (int)(baseCycle * 0.5f + baseCycle * 0.5f * percent));
        }

        double alphaMin = flashAlphaMin.get();
        double alphaMax = flashAlphaMax.get();

        // 简单的三角波函数（周期性在 min/max 之间变化）
        float phase = (age % cycleTicks) / (float) cycleTicks;
        float alphaFactor = (float)(0.5f - 0.5f * Math.cos(phase * 2 * Math.PI)); // cosine wave
        return (float)(alphaMin + (alphaMax - alphaMin) * alphaFactor);
    }
}
