package com.rinko1231.despawnnotifier.mixin.client;

import com.rinko1231.despawnnotifier.impl.IAlphaModifier;
import net.minecraft.client.renderer.entity.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin implements IAlphaModifier {
    @Unique private float factor = 1f;

//    @ModifyVariable(method = "renderQuadList", at = @At(value = "STORE"), ordinal = 0)
//    public float modifyAlpha(float alpha){
//        return alpha * 0.1f;
//    }

    @ModifyArg(method = "renderQuadList", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;putBulkData(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lnet/minecraft/client/renderer/block/model/BakedQuad;FFFFIIZ)V"), index = 5)
    public float alpha(float value){
        return value * 0.1f;
    }

    @Override
    public void mul(float factor) {
        this.factor = factor;
    }
}
