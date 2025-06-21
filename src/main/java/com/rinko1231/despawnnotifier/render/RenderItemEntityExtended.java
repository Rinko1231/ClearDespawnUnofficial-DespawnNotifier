package com.rinko1231.despawnnotifier.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rinko1231.despawnnotifier.config.DespawnNotifierConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.item.ItemEntity;

public class RenderItemEntityExtended extends ItemEntityRenderer {
    public RenderItemEntityExtended(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ItemEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        int remainingTime = entity.lifespan - entity.getAge();
        int itemFlashStartTime = DespawnNotifierConfig.itemFlashStartTime.get();
        if (remainingTime <= 20 * itemFlashStartTime) {
            if (DespawnNotifierConfig.isUrgentFlashEnabled.get()) {
                if (remainingTime > itemFlashStartTime * 10 ) {  // "*20/2" = "*10"
                    if (remainingTime % 12 < 4) {
                        return;
                    }
                } else if (remainingTime > itemFlashStartTime * 5 ) {  // "*20/4" = "*5"
                    if (remainingTime % 9 < 3) {
                        return;
                    }
                } else {  // 剩余0~5秒之间
                    if (remainingTime % 6 < 2) {
                        return;
                    }
                }
            } else if (remainingTime % 20 < 10) {
                return;
            }
        }
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    public static class Factory implements EntityRendererProvider<ItemEntity> {
        @Override
        public EntityRenderer<ItemEntity> create(Context context) {
            return new RenderItemEntityExtended(context);
        }
    }
}