package dev.paw.fxmod.mixin;

import dev.paw.fxmod.FXMod;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Shadow
    private double eventDeltaWheel;

    @Inject(method = "onMouseScroll", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;eventDeltaWheel:D", ordinal = 7), cancellable = true)
    private void onMouseScroll(CallbackInfo ci) {
        if (eventDeltaWheel != 0.0) {
            if (FXMod.OPTIONS.freecam.getValue()) {
                FXMod.VARS.freecamSpeedBoost += eventDeltaWheel > 0.0 ? 0.05D : -0.05D;
                FXMod.VARS.freecamSpeedBoost = Math.max(0,FXMod.VARS.freecamSpeedBoost);
                FXMod.VARS.freecamSpeedBoost = Math.min(FXMod.VARS.freecamSpeedBoost, 20);
            }
        }
    }
}
