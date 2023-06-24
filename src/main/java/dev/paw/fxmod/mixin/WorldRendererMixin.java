package dev.paw.fxmod.mixin;

import dev.paw.fxmod.FXMod;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getTeamColorValue()I", ordinal = 0))
    private int onPlayerGlow(Entity entity)
    {
        // NOTE: Does now work in Canvas as it replaces the WorldRenderer instance and it has it's own render method

        if(entity.equals(FXMod.MC.player) && FXMod.OPTIONS.freecam.getValue() && FXMod.OPTIONS.freecamOutline.getValue()) {
            return 65280;
        }

        return entity.getTeamColorValue();
    }

    //pano stuff
    @Inject(method = "drawBlockOutline", at = @At("HEAD"), cancellable = true)
    public void onDrawBlockOutline(MatrixStack matrices, VertexConsumer vertexConsumer, Entity entity, double cameraX, double cameraY, double cameraZ, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (FXMod.INSTANCE.panoramaMaker.isRunning) {
            ci.cancel();
        }
    }
}
