package dev.paw.fxmod.mixin;

import dev.paw.fxmod.FXMod;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerEntityRenderer.class)
abstract class PlayerEntityRendererMixin<T extends Entity> extends EntityRenderer<T> {
    protected PlayerEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected boolean hasLabel(T entity)
    {
        // while in freecam makes your own nametag visible
        if(FXMod.OPTIONS.freecam.getValue() && entity == FXMod.MC.player && !FXMod.MC.options.hudHidden) {
            return true;
        }

        return super.hasLabel(entity);
    }
}
