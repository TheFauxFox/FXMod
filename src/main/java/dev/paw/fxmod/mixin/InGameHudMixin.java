package dev.paw.fxmod.mixin;

import dev.paw.fxmod.FXMod;
import dev.paw.fxmod.utils.Color;
import dev.paw.fxmod.utils.OnScreenText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
abstract class InGameHudMixin implements Drawable {
	@Final
	@Shadow
	private MinecraftClient client;

	@Shadow
	protected abstract LivingEntity getRiddenEntity();

	@Shadow
	protected abstract int getHeartCount(LivingEntity entity);

	@Shadow
	protected abstract int getHeartRows(int heartCount);

	@Shadow
	protected abstract PlayerEntity getCameraPlayer();

	@Inject(method = "tick()V", at = @At("HEAD"))
	private void onTick(CallbackInfo info)
	{
		FXMod.VARS.tickToolWarningTicks();
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void onRender(DrawContext context, float f, CallbackInfo info)
	{
		// renders on screen text only if not in debug or hud is hidden or if options don't say so
		if(this.client.options.debugEnabled || this.client.options.hudHidden) {
			return;
		}

		if(FXMod.VARS.getToolWarningTextTicksLeft() > 0) {
			context.getMatrices().push();
			context.getMatrices().translate((this.client.getWindow().getScaledWidth() / 2.0d), (this.client.getWindow().getScaledHeight() / 2.0d), 0);
			context.getMatrices().scale(1.5f, 1.5f, 1.0f);
			OnScreenText.drawToolWarningText(context);
			context.getMatrices().pop();
		}

		if (true) { // placeholder for option for FPS
			int x = 1;
			int y = 1;
			context.getMatrices().push();
			context.getMatrices().translate(x, y, 0);
			context.getMatrices().scale(1, 1, 1);
			context.getMatrices().translate(-x, -y, 0);
			context.drawTextWithShadow(FXMod.MC.textRenderer, ((MinecraftClientAccessor) FXMod.MC).getCurrentFPS() + " FPS", x, y, Color.WHITE.getPacked());
			context.getMatrices().pop();
		}
	}

	@Redirect(method = "renderStatusBars(Lnet/minecraft/client/gui/DrawContext;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartRows(I)I", ordinal = 0))
	private int hijackGetHeartRows(InGameHud igHud, int heartCount)
	{
		// super rare thing but the air bubbles would overlap mount health if shown (ex. popping out of water and straight onto a horse), so yeah this fixes that
		if(this.getCameraPlayer() != null && this.getHeartCount(this.getRiddenEntity()) != 0 && FXMod.MC.interactionManager.hasStatusBars()) {
			return this.getHeartRows(heartCount) + 1;
		}
		else {
			return this.getHeartRows(heartCount);
		}
	}
}
