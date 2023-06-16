package dev.paw.fxmod.mixin;

import dev.paw.fxmod.FXMod;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
abstract class CameraMixin
{
    @Shadow
    private boolean ready;
    @Shadow
    private BlockView area;
    @Shadow
    private Entity focusedEntity;
    @Shadow
    private boolean thirdPerson;

    private boolean _preFreecam = true;

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Shadow
    protected abstract void setPos(double x, double y, double z);

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void onUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info)
    {
        // freecam
        if(FXMod.OPTIONS.freecam.getValue() && FXMod.MC.player != null) {
            if(_preFreecam) {
                _preFreecam = false;

                FXMod.MC.chunkCullingEnabled = false;

                if(FXMod.MC.player.getVehicle() instanceof BoatEntity) {
                    ((BoatEntity)FXMod.MC.player.getVehicle()).setInputs(false, false, false, false);
                }

                FXMod.VARS.playerVelocity = FXMod.MC.player.getVelocity();

                FXMod.VARS.freecamPitch = inverseView ? -FXMod.MC.player.getPitch() : FXMod.MC.player.getPitch();
                FXMod.VARS.freecamYaw = inverseView ? FXMod.MC.player.getYaw() + 180.0f : FXMod.MC.player.getYaw();

                FXMod.VARS.freecamX = FXMod.VARS.prevFreecamX = FXMod.MC.gameRenderer.getCamera().getPos().getX();
                FXMod.VARS.freecamY = FXMod.VARS.prevFreecamY = FXMod.MC.gameRenderer.getCamera().getPos().getY() + (thirdPerson ? 0.0f : 0.7f);
                FXMod.VARS.freecamZ = FXMod.VARS.prevFreecamZ = FXMod.MC.gameRenderer.getCamera().getPos().getZ();
            }

            this.ready = true;
            this.area = area;
            this.focusedEntity = focusedEntity;
            this.thirdPerson = thirdPerson;

            this.setRotation((float)FXMod.VARS.freecamYaw, (float)FXMod.VARS.freecamPitch);
            this.setPos(MathHelper.lerp(tickDelta, FXMod.VARS.prevFreecamX, FXMod.VARS.freecamX), MathHelper.lerp(tickDelta, FXMod.VARS.prevFreecamY, FXMod.VARS.freecamY), MathHelper.lerp(tickDelta, FXMod.VARS.prevFreecamZ, FXMod.VARS.freecamZ));

            info.cancel();
        }
        else if(!_preFreecam) {
            _preFreecam = true;

            FXMod.MC.chunkCullingEnabled = true;

            FXMod.VARS.freecamForwardSpeed = 0.0f;
            FXMod.VARS.freecamUpSpeed = 0.0f;
            FXMod.VARS.freecamSideSpeed = 0.0f;
        }
    }

    // makes you able to see yourself while in freecam
    @Inject(method = "isThirdPerson", at = @At("HEAD"), cancellable = true)
    private void onIsThirdPerson(CallbackInfoReturnable<Boolean> info)
    {
        if(FXMod.OPTIONS.freecam.getValue()) {
            info.setReturnValue(true);
        }
    }
}
