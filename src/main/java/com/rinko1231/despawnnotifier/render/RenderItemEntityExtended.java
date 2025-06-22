package com.rinko1231.despawnnotifier.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rinko1231.despawnnotifier.config.DespawnNotifierConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.world.entity.item.ItemEntity;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.min;

public class RenderItemEntityExtended extends ItemEntityRenderer {
    public RenderItemEntityExtended(EntityRendererProvider.Context context) {
        super(context);
    }
    private int itemEntityLifespan;
    private int itemEntityAge;

    @Override
    public void extractRenderState(ItemEntity entity, @NotNull ItemEntityRenderState state, float partialTicks) {
        this.itemEntityLifespan = entity.lifespan;
        this.itemEntityAge = entity.getAge();
        super.extractRenderState(entity, state, partialTicks);
    }


    @Override
    public void render(@NotNull ItemEntityRenderState itemEntityRenderState, @NotNull PoseStack p_115030_, @NotNull MultiBufferSource p_115031_, int p_115032_) {
        int remainingTimeTick = this.itemEntityLifespan - this.itemEntityAge;
        int itemFlashStartTimeSecond = min(this.itemEntityLifespan/20, DespawnNotifierConfig.itemFlashStartTime.get());
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
        super.render(itemEntityRenderState, p_115030_, p_115031_, p_115032_) ;
    }


   public static class Factory implements EntityRendererProvider<ItemEntity> {
       @Override
       public @NotNull EntityRenderer<ItemEntity, ?> create(@NotNull Context context) {
           return new RenderItemEntityExtended(context);
       }
   }

}

