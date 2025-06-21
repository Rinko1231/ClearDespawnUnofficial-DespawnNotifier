package com.rinko1231.despawnnotifier.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rinko1231.despawnnotifier.config.DespawnNotifierConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.item.ItemEntity;

import static java.lang.Math.min;

public class RenderItemEntityExtended extends ItemEntityRenderer {
    public RenderItemEntityExtended(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ItemEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        int remainingTimeTick = entity.lifespan - entity.getAge();
        int itemFlashStartTimeSecond = min(entity.lifespan/20, DespawnNotifierConfig.itemFlashStartTime.get());
        if (remainingTimeTick <= 20 * itemFlashStartTimeSecond) {
            if (DespawnNotifierConfig.isUrgentFlashEnabled.get()) {
                if (remainingTimeTick > (itemFlashStartTimeSecond * 10) ) {  // "itemFlashStartTimeSecond*20/2" = "itemFlashStartTimeSecond*10"
                    if (remainingTimeTick % 20 < 7) {
                        return;
                    }
                } else if (remainingTimeTick > (itemFlashStartTimeSecond * 5) ) {  // "itemFlashStartTimeSecond*20/4" = "itemFlashStartTimeSecond*5"
                    if (remainingTimeTick % 12 < 4) {
                        return;
                    }
                } else {  // 剩余1/4闪烁时间
                    if (remainingTimeTick % 3 < 2) {
                        return;
                    }
                }
            } else if (remainingTimeTick % 20 < 7) {
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