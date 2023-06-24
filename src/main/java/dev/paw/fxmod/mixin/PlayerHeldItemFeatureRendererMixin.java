package dev.paw.fxmod.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.paw.fxmod.libzoomer.impl.SpyglassHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;

@Mixin(PlayerHeldItemFeatureRenderer.class)
public abstract class PlayerHeldItemFeatureRendererMixin {
	@Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
	private void renderCustomSpyglassesAsSpyglass(LivingEntity entity, ItemStack stack, ModelTransformationMode modelTransformationMode, Arm arm,
												  MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if (stack.isIn(SpyglassHelper.SPYGLASSES) && entity.getActiveItem() == stack && entity.handSwingTicks == 0) {
			this.renderSpyglass(entity, stack, arm, matrices, vertexConsumers, light);
			ci.cancel();
		}
	}

	@Shadow
	protected abstract void renderSpyglass(LivingEntity livingEntity, ItemStack itemStack, Arm arm, MatrixStack matrixStack,
										   VertexConsumerProvider vertexConsumerProvider, int light);
}
