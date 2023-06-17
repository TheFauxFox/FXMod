package dev.paw.fxmod.mixin;

import dev.paw.fxmod.FXMod;
import dev.paw.fxmod.utils.ZoomUtils;
import io.github.ennuil.libzoomer.api.ZoomInstance;
import io.github.ennuil.libzoomer.api.ZoomRegistry;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Mouse.class)
public abstract class MouseMixin {

    @Unique
    private boolean modifyMouse;

    @Unique
    private double finalCursorDeltaX;

    @Unique
    private double finalCursorDeltaY;

    @Shadow
    private double eventDeltaWheel;

    @Inject(method = "onMouseScroll", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Mouse;eventDeltaWheel:D", ordinal = 7), cancellable = true)
    private void onMouseScroll(CallbackInfo ci) {
        if (eventDeltaWheel != 0.0) {
            if (FXMod.OPTIONS.freecam.getValue() && !FXMod.INSTANCE.isZooming()) {
                FXMod.VARS.freecamSpeedBoost += eventDeltaWheel > 0.0 ? 0.05D : -0.05D;
                FXMod.VARS.freecamSpeedBoost = Math.max(0,FXMod.VARS.freecamSpeedBoost);
                FXMod.VARS.freecamSpeedBoost = Math.min(FXMod.VARS.freecamSpeedBoost, 20);
                ci.cancel();
            }

            if (FXMod.INSTANCE.isZooming()) {
                ZoomUtils.changeZoomDivisor(eventDeltaWheel > 0);
                ci.cancel();
            }
        }
    }

    @Inject(
            method = "updateMouse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/GameOptions;getInvertYMouse()Lnet/minecraft/client/option/SimpleOption;"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void applyZoomChanges(CallbackInfo ci, double d, double e, double k, double l, double f, double g, double h, int m) {
        this.modifyMouse = false;
        if (ZoomRegistry.shouldIterateZoom() || ZoomRegistry.shouldIterateModifiers()) {
            for (ZoomInstance instance : ZoomRegistry.getZoomInstances()) {
                if (instance.getMouseModifier() != null) {
                    boolean zoom = instance.getZoom();
                    if (zoom || instance.isModifierActive()) {
                        instance.getMouseModifier().tick(zoom);
                        double zoomDivisor = zoom ? instance.getZoomDivisor() : 1.0;
                        double transitionDivisor = instance.getTransitionMode().getInternalMultiplier();
                        k = instance.getMouseModifier().applyXModifier(k, h, e, zoomDivisor, transitionDivisor);
                        l = instance.getMouseModifier().applyYModifier(l, h, e, zoomDivisor, transitionDivisor);
                        this.modifyMouse = true;
                    }
                }
            }
        }
        this.finalCursorDeltaX = k;
        this.finalCursorDeltaY = l;
    }

    @ModifyVariable(
            method = "updateMouse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/GameOptions;getInvertYMouse()Lnet/minecraft/client/option/SimpleOption;"
            ),
            ordinal = 2
    )
    private double modifyFinalCursorDeltaX(double k) {
        return this.modifyMouse ? finalCursorDeltaX : k;
    }

    @ModifyVariable(
            method = "updateMouse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/option/GameOptions;getInvertYMouse()Lnet/minecraft/client/option/SimpleOption;"
            ),
            ordinal = 3
    )
    private double modifyFinalCursorDeltaY(double l) {
        return this.modifyMouse ? finalCursorDeltaY : l;
    }
}
