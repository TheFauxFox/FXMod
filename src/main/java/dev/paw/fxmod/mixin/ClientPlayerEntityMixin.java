package dev.paw.fxmod.mixin;

import com.mojang.authlib.GameProfile;

import dev.paw.fxmod.FXMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.JumpingMount;
import net.minecraft.util.math.MathHelper;

@Mixin(ClientPlayerEntity.class)
abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity
{
    @Shadow
    public abstract JumpingMount getJumpingMount();

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void onDropSelectedItem(CallbackInfoReturnable<Boolean> info)
    {
        if(FXMod.OPTIONS.freecam.getValue()) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "updateHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getHealth()F", ordinal = 0))
    private void onUpdateHealth(float health, CallbackInfo info)
    {
        // disables freecam if you take damage while using it
        if(this.hurtTime == 10 && FXMod.OPTIONS.freecam.getValue()) {
            FXMod.OPTIONS.freecam.setValue(false);
        }
    }

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onTickMovement(CallbackInfo info)
    {

        if(FXMod.OPTIONS.freecam.getValue() && FXMod.MC.player != null) {
            this.setVelocity(FXMod.VARS.playerVelocity);

            float forward = FXMod.MC.player.input.movementForward;
            float up = (FXMod.MC.player.input.jumping ? 1.0f : 0.0f) - (FXMod.MC.player.input.sneaking ? 1.0f : 0.0f);
            float side = FXMod.MC.player.input.movementSideways;

            FXMod.VARS.freecamForwardSpeed = forward != 0 ? _updateMotion(FXMod.VARS.freecamForwardSpeed, forward) : FXMod.VARS.freecamForwardSpeed * 0.5f;
            FXMod.VARS.freecamUpSpeed = up != 0 ?  _updateMotion(FXMod.VARS.freecamUpSpeed, up) : FXMod.VARS.freecamUpSpeed * 0.5f;
            FXMod.VARS.freecamSideSpeed = side != 0 ?  _updateMotion(FXMod.VARS.freecamSideSpeed , side) : FXMod.VARS.freecamSideSpeed * 0.5f;

            double rotateX = Math.sin(FXMod.VARS.freecamYaw * Math.PI / 180.0D);
            double rotateZ = Math.cos(FXMod.VARS.freecamYaw * Math.PI / 180.0D);
            double speed = FXMod.MC.player.isSprinting() ? 1.2D : 0.55D;

            FXMod.VARS.prevFreecamX = FXMod.VARS.freecamX;
            FXMod.VARS.prevFreecamY = FXMod.VARS.freecamY;
            FXMod.VARS.prevFreecamZ = FXMod.VARS.freecamZ;

            FXMod.VARS.freecamX += (FXMod.VARS.freecamSideSpeed * rotateZ - FXMod.VARS.freecamForwardSpeed * rotateX) * speed;
            FXMod.VARS.freecamY += FXMod.VARS.freecamUpSpeed * speed;
            FXMod.VARS.freecamZ += (FXMod.VARS.freecamForwardSpeed * rotateZ + FXMod.VARS.freecamSideSpeed * rotateX) * speed;
        }
    }

    private float _updateMotion(float motion, float direction)
    {
        return (direction + motion == 0) ? 0.0f : MathHelper.clamp(motion + ((direction < 0) ? -0.35f : 0.35f), -1f, 1f);
    }

    // PREVENTS SENDING VEHICLE MOVEMENT PACKETS TO SERVER (freecam)
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasVehicle()Z", ordinal = 0))
    private boolean hijackHasVehicle(ClientPlayerEntity player)
    {
        if(FXMod.OPTIONS.freecam.getValue()) {
            return false;
        }

        return this.hasVehicle();
    }

    // PREVENTS HORSES FROM JUMPING (freecam)
    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getJumpingMount()Lnet/minecraft/entity/JumpingMount;", ordinal = 0))
    private JumpingMount hijackGetJumpingMount(ClientPlayerEntity player)
    {
        if(FXMod.OPTIONS.freecam.getValue()) {
            return null;
        }

        return this.getJumpingMount();
    }

    // PREVENTS BOAT MOVEMENT (freecam)
    @Inject(method = "tickRiding", at = @At("HEAD"), cancellable = true)
    private void onTickRiding(CallbackInfo info)
    {
        if(FXMod.OPTIONS.freecam.getValue()) {
            super.tickRiding();
            info.cancel();
        }
    }

    // PREVENTS MOVEMENT (freecam)
    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    private void onMove(CallbackInfo info)
    {
        if(FXMod.OPTIONS.freecam.getValue()) {
            info.cancel();
        }
    }

    // PREVENTS MORE MOVEMENT (freecam)
    @Inject(method = "isCamera", at = @At("HEAD"), cancellable = true)
    private void onIsCamera(CallbackInfoReturnable<Boolean> info)
    {
        if(FXMod.OPTIONS.freecam.getValue()) {
            info.setReturnValue(false);
        }
    }

    // PREVENTS SNEAKING (freecam)
    @Inject(method = "isSneaking", at = @At("HEAD"), cancellable = true)
    private void onIsSneaking(CallbackInfoReturnable<Boolean> info)
    {
        if(FXMod.OPTIONS.freecam.getValue()) {
            info.setReturnValue(false);
        }
    }

    // UPDATES YAW AND PITCH BASED ON MOUSE MOVEMENT (freecam)
    @Override
    public void changeLookDirection(double cursorDeltaX, double cursorDeltaY)
    {
        if(FXMod.OPTIONS.freecam.getValue()) {
            FXMod.VARS.freecamYaw += cursorDeltaX * 0.15D;
            FXMod.VARS.freecamPitch = MathHelper.clamp(FXMod.VARS.freecamPitch + cursorDeltaY * 0.15D, -90, 90);
        }
        else {
            super.changeLookDirection(cursorDeltaX, cursorDeltaY);
        }
    }

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) { super(world, profile); } // IGNORED
}

