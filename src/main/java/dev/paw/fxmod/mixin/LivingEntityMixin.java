package dev.paw.fxmod.mixin;

import dev.paw.fxmod.FXMod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow protected abstract void updatePotionVisibility();

    @Redirect(method = "updatePotionVisibility", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/data/DataTracker;set(Lnet/minecraft/entity/data/TrackedData;Ljava/lang/Object;)V", ordinal = 1))
    public <T> void updatePotionVisibility(DataTracker instance, TrackedData<T> key, T value) {
        if (FXMod.OPTIONS.noPotionParticles.getValue()) {
            instance.set((TrackedData<Integer>) key, 0);
        } else {
            instance.set(key, value);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        updatePotionVisibility();
    }
}
