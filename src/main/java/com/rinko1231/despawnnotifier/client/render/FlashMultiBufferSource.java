package com.rinko1231.despawnnotifier.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rinko1231.despawnnotifier.impl.IRenderStateExtra;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor;
import net.neoforged.neoforge.client.model.pipeline.VertexConsumerWrapper;
import org.jetbrains.annotations.NotNull;

public record FlashMultiBufferSource(MultiBufferSource original, float alphaFactor) implements MultiBufferSource {
    private void extraTranslucent(RenderType renderType) {
        if (renderType instanceof IRenderStateExtra iRenderStateExtra) {
            iRenderStateExtra.addExtraState(() -> {
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(
                        GlStateManager.SourceFactor.SRC_ALPHA,
                        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                        GlStateManager.SourceFactor.ONE,
                        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
                );
            });
        }
    }

    @Override
    public VertexConsumer getBuffer(@NotNull RenderType renderType) {
        this.extraTranslucent(renderType);
        VertexConsumer buffer = original().getBuffer(renderType);
        return new VertexConsumerWrapper(buffer) {
            @Override
            public void addVertex(float x, float y, float z, int color, float u, float v, int packedOverlay, int packedLight, float normalX, float normalY, float normalZ) {
                int alpha = FastColor.ARGB32.alpha(color);
                int newColor = FastColor.ARGB32.color((int) (alpha * alphaFactor), color);
                super.addVertex(x, y, z, newColor, u, v, packedOverlay, packedLight, normalX, normalY, normalZ);
            }
        };
    }
}
