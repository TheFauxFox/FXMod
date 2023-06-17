package dev.paw.fxmod.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.paw.fxmod.FXMod;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.client.render.FogShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BackgroundRenderer.class, priority = 910)
public abstract class BackgroundRendererMixin
{
    @Inject(method = "applyFog", at = @At("RETURN"))
    private static void applyFogModifyDistance(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci)
    {
        final CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
        final Entity entity = camera.getFocusedEntity();

        if (FXMod.OPTIONS.noFog.getValue())
        {
            RenderSystem.setShaderFogStart(-8.0F);
            RenderSystem.setShaderFogEnd(1000000.0F);
            RenderSystem.setShaderFogShape(FogShape.CYLINDER);
        }
    }

//    @Unique
//    private static FogToggleType getFogType(BackgroundRenderer.FogType fogType, boolean thickFog, CameraSubmersionType cameraSubmersionType, Entity entity)
//    {
//        if (cameraSubmersionType == CameraSubmersionType.LAVA)
//        {
//            return FogToggleType.LAVA;
//        }
//
//        if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW)
//        {
//            return FogToggleType.POWDER_SNOW;
//        }
//
//        if (entity instanceof LivingEntity && ((LivingEntity) entity).hasStatusEffect(StatusEffects.BLINDNESS))
//        {
//            return FogToggleType.BLINDNESS;
//        }
//
//        if (entity instanceof LivingEntity && ((LivingEntity) entity).hasStatusEffect(StatusEffects.DARKNESS))
//        {
//            return FogToggleType.DARKNESS;
//        }
//
//        if (cameraSubmersionType == CameraSubmersionType.WATER)
//        {
//            return FogToggleType.WATER;
//        }
//
//        if (thickFog)
//        {
//            return FogToggleType.THICK;
//        }
//
//        if (fogType == BackgroundRenderer.FogType.FOG_SKY)
//        {
//            return FogToggleType.SKY;
//        }
//
//        return FogToggleType.TERRAIN;
//    }
}
