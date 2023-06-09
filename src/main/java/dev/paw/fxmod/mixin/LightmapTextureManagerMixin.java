package dev.paw.fxmod.mixin;

import dev.paw.fxmod.FXMod;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightmapTextureManager.class)
abstract class LightmapTextureManagerMixin
{
    @Inject(method = "getBrightness", at = @At("HEAD"), cancellable = true)
    private static void onGetBrightness(CallbackInfoReturnable<Float> info)
    {
        if (FXMod.OPTIONS.fullbright.getValue()) {
            info.setReturnValue(1f);
        }
    }
}
