package com.rinko1231.despawnnotifier.mixin.client;

import com.mojang.blaze3d.vertex.MeshData;
import com.rinko1231.despawnnotifier.impl.IRenderStateExtra;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(RenderType.class)
public class RenderTypeMixin implements IRenderStateExtra {
    @Unique
    private final List<Runnable> extraStates = new ArrayList<>();

    @Inject(method = "draw", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderType;setupRenderState()V", shift = At.Shift.AFTER))
    public void runExtraState(MeshData meshData, CallbackInfo ci) {
        if (!extraStates.isEmpty()) {
            extraStates.forEach(Runnable::run);
            extraStates.clear();
        }
    }

    @Override
    public void addExtraState(Runnable runnable) {
        extraStates.add(runnable);
    }
}