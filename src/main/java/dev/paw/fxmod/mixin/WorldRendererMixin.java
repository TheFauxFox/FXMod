package dev.paw.fxmod.mixin;

import dev.paw.fxmod.FXMod;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
}
