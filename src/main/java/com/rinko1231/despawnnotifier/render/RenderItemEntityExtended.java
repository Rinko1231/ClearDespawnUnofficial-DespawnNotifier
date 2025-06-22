package com.rinko1231.despawnnotifier.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rinko1231.despawnnotifier.config.DespawnNotifierConfig;
import com.rinko1231.despawnnotifier.network.ClientPayloadHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;

public class RenderItemEntityExtended extends ItemEntityRenderer {
    public RenderItemEntityExtended(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ItemEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int packedLight) {
        // 获取网络同步的年龄数据
        ClientPayloadHandler.ItemEntityAgeData ageData = ClientPayloadHandler.getItemAgeData(entity.getId());
        
        if (ageData != null) {
            // 检查是否是移除标记
            if (ageData.age() == -1 && ageData.lifespan() == -1) {
                // 实体已被移除，清理缓存并跳过渲染
                ClientPayloadHandler.removeItemAgeData(entity.getId());
                return;
            }
            
            // 使用网络同步的精确数据
            int currentAge = ageData.getCurrentAge();
            int remainingTicks = ageData.getRemainingLife();
            int warningStartTicks = DespawnNotifierConfig.itemFlashStartTime.get() * 20;
            
            if (remainingTicks <= warningStartTicks && remainingTicks > 0) {
                float alpha;
                
                if (DespawnNotifierConfig.useBreathingEffect.get()) {
                    // 呼吸效果（淡入淡出）- 使用精确的年龄数据
                    alpha = calculateBreathingAlpha(currentAge, remainingTicks, warningStartTicks, ageData.lifespan());
                } else {
                    // 传统闪烁效果
                    alpha = calculateBlinkingAlpha(currentAge, remainingTicks, warningStartTicks, ageData.lifespan());
                }
                
                // 使用透明度渲染
                MultiBufferSource alphaBuffer = new EnhancedAlphaBufferSource(buffer, alpha);
                super.render(entity, entityYaw, partialTicks, matrixStack, alphaBuffer, packedLight);
                return;
            }
        } else {
            // 如果没有网络数据，定期清理过期缓存
            if (entity.tickCount % 100 == 0) { // 每5秒清理一次
                ClientPayloadHandler.cleanupExpiredData();
            }
        }
        
        // 正常渲染（没有网络数据或不在警告期内）
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }
    
    /**
     * 计算呼吸效果的透明度 - 基于网络同步的精确年龄数据
     */
    private float calculateBreathingAlpha(int currentAge, int remainingTicks, int warningStartTicks, int lifespan) {
        float timeRatio = (float) remainingTicks / warningStartTicks; // 1.0 到 0.0
        
        if (DespawnNotifierConfig.isUrgentFlashEnabled.get()) {
            // 三阶段加速模式 - 基于精确年龄
            if (timeRatio > 0.66f) {
                // 第一阶段：慢呼吸 (1秒周期)
                float cycle = (currentAge % 20) / 20.0f;
                return 0.4f + 0.6f * (0.5f + 0.5f * Mth.sin(cycle * 2 * Mth.PI));
            } else if (timeRatio > 0.33f) {
                // 第二阶段：中等呼吸 (0.6秒周期)
                float cycle = (currentAge % 12) / 12.0f;
                return 0.3f + 0.7f * (0.5f + 0.5f * Mth.sin(cycle * 2 * Mth.PI));
            } else {
                // 第三阶段：快速呼吸 (0.15秒周期) + 渐变消失
                float cycle = (currentAge % 3) / 3.0f;
                float breathingAlpha = 0.2f + 0.8f * (0.5f + 0.5f * Mth.sin(cycle * 2 * Mth.PI));
                // 在最后阶段添加渐变消失效果
                return breathingAlpha * timeRatio;
            }
        } else {
            // 统一呼吸模式
            float cycle = (currentAge % 20) / 20.0f;
            float breathingAlpha = 0.3f + 0.7f * (0.5f + 0.5f * Mth.sin(cycle * 2 * Mth.PI));
            // 添加整体渐变消失效果
            return breathingAlpha * (0.3f + 0.7f * timeRatio);
        }
    }
    
    /**
     * 计算闪烁效果的透明度
     */
    private float calculateBlinkingAlpha(int currentAge, int remainingTicks, int warningStartTicks, int lifespan) {
        float timeRatio = (float) remainingTicks / warningStartTicks;
        
        if (DespawnNotifierConfig.isUrgentFlashEnabled.get()) {
            // 三阶段加速模式 - 基于精确年龄
            if (timeRatio > 0.66f) {
                // 第一阶段：慢闪烁
                return (currentAge % 20 < 10) ? 1.0f : 0.3f;
            } else if (timeRatio > 0.33f) {
                // 第二阶段：中等闪烁
                return (currentAge % 12 < 6) ? 1.0f : 0.2f;
            } else {
                // 第三阶段：快速闪烁 + 渐变消失
                float blinkAlpha = (currentAge % 3 < 2) ? 1.0f : 0.1f;
                return blinkAlpha * timeRatio; // 渐变消失
            }
        } else {
            // 统一闪烁
            float blinkAlpha = (currentAge % 20 < 10) ? 1.0f : 0.3f;
            return blinkAlpha * (0.3f + 0.7f * timeRatio); // 整体渐变消失
        }
    }
    
    private static class EnhancedAlphaBufferSource implements MultiBufferSource {
        private final MultiBufferSource wrapped;
        private final float alpha;
        
        public EnhancedAlphaBufferSource(MultiBufferSource wrapped, float alpha) {
            this.wrapped = wrapped;
            this.alpha = alpha;
        }
        
        @Override
        public VertexConsumer getBuffer(RenderType renderType) {
            // 为方块物品使用透明渲染类型，神必东西💩
            RenderType transparentType = getTransparentRenderType(renderType);
            VertexConsumer consumer = wrapped.getBuffer(transparentType);
            return new EnhancedAlphaVertexConsumer(consumer, alpha);
        }
        
        /**
         * 获取对应的透明渲染类型，确保方块物品能正确透明渲染
         */
        private RenderType getTransparentRenderType(RenderType original) {
            if (original.toString().contains("translucent") || original.toString().contains("transparent")) {
                return original;
            }
            try {
                return RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS);
            } catch (Exception e) {
                // 如果失败，使用原始类型
                return original;
            }
        }
    }
    
    private static class EnhancedAlphaVertexConsumer implements VertexConsumer {
        private final VertexConsumer wrapped;
        private final float alpha;
        
        public EnhancedAlphaVertexConsumer(VertexConsumer wrapped, float alpha) {
            this.wrapped = wrapped;
            this.alpha = alpha;
        }
        
        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            return wrapped.addVertex(x, y, z);
        }
        
        @Override
        public VertexConsumer setColor(int red, int green, int blue, int alpha) {
            // 增强的alpha处理，确保方块物品正确透明
            float finalAlpha = (alpha / 255.0f) * this.alpha;
            int newAlpha = Math.max(1, Math.min(255, (int) (finalAlpha * 255.0f)));
            return wrapped.setColor(red, green, blue, newAlpha);
        }
        
        @Override
        public VertexConsumer setUv(float u, float v) {
            return wrapped.setUv(u, v);
        }
        
        @Override
        public VertexConsumer setUv1(int u, int v) {
            return wrapped.setUv1(u, v);
        }
        
        @Override
        public VertexConsumer setUv2(int u, int v) {
            return wrapped.setUv2(u, v);
        }
        
        @Override
        public VertexConsumer setNormal(float x, float y, float z) {
            return wrapped.setNormal(x, y, z);
        }
        
        @Override
        public void addVertex(float x, float y, float z, int color, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
            int alphaValue = (color >> 24) & 255;
            if (alphaValue == 0) alphaValue = 255;
            float finalAlpha = (alphaValue / 255.0f) * this.alpha;
            int newAlpha = Math.max(1, Math.min(255, (int) (finalAlpha * 255.0f)));
            int newColor = (color & 0x00FFFFFF) | (newAlpha << 24);
            wrapped.addVertex(x, y, z, newColor, u, v, overlay, light, normalX, normalY, normalZ);
        }
    }

    public static class Factory implements EntityRendererProvider<ItemEntity> {
        @Override
        public EntityRenderer<ItemEntity> create(Context context) {
            return new RenderItemEntityExtended(context);
        }
    }
}