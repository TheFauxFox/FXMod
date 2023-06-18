package dev.paw.fxmod.mixin;

import dev.paw.fxmod.FXMod;
import io.github.ennuil.libzoomer.api.ZoomInstance;
import io.github.ennuil.libzoomer.api.ZoomRegistry;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void removeHandRendering(CallbackInfo info)
    {
        if(FXMod.OPTIONS.freecam.getValue()) {
            info.cancel();
        }
    }

    @Redirect(method = "tiltViewWhenHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getDamageTiltYaw()F"))
    public float changeHurtCamType(LivingEntity instance) {
        return FXMod.OPTIONS.betterHurtCam.getValue() ? 0 : instance.getDamageTiltYaw();
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void tickInstances(CallbackInfo info) {
        boolean iterateZoom = false;
        boolean iterateTransitions = false;
        boolean iterateModifiers = false;
        boolean iterateOverlays = false;

        for (ZoomInstance instance : ZoomRegistry.getZoomInstances()) {
            boolean zoom = instance.getZoom();
            if (zoom || (instance.isTransitionActive() || instance.isOverlayActive())) {
                double divisor = zoom ? instance.getZoomDivisor() : 1.0;
                if (instance.getZoomOverlay() != null) {
                    instance.getZoomOverlay().tick(zoom, divisor, instance.getTransitionMode().getInternalMultiplier());
                }
                instance.getTransitionMode().tick(zoom, divisor);
            }

            iterateZoom = iterateZoom || zoom;
            iterateTransitions = iterateTransitions || instance.isTransitionActive();
            iterateModifiers = iterateModifiers || instance.isModifierActive();
            iterateOverlays = iterateOverlays || instance.isOverlayActive();
        }

        ZoomRegistry.setIterateZoom(iterateZoom);
        ZoomRegistry.setIterateTransitions(iterateTransitions);
        ZoomRegistry.setIterateModifiers(iterateModifiers);
    }

    @Inject(method = "getFov(Lnet/minecraft/client/render/Camera;FZ)D", at = @At("RETURN"), cancellable = true)
    private void getZoomedFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        double fov = cir.getReturnValue();
        double zoomedFov = fov;

        if (ZoomRegistry.shouldIterateTransitions()) {
            for (ZoomInstance instance : ZoomRegistry.getZoomInstances()) {
                if (instance.isTransitionActive()) {
                    zoomedFov = instance.getTransitionMode().applyZoom(zoomedFov, tickDelta);
                }
            }
        }

        if (fov != zoomedFov) {
            cir.setReturnValue(zoomedFov);
        }
    }
}
