package dev.paw.fxmod.mixin;

import com.google.common.collect.Ordering;
import dev.paw.fxmod.FXMod;
import dev.paw.fxmod.utils.Color;
import dev.paw.fxmod.utils.OnScreenText;
import io.github.ennuil.libzoomer.api.ZoomInstance;
import io.github.ennuil.libzoomer.api.ZoomOverlay;
import io.github.ennuil.libzoomer.api.ZoomRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(value = InGameHud.class, priority = 500)
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

		if (FXMod.OPTIONS.fpsdisplay.getValue()) { // placeholder for option for FPS
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
		if(this.getCameraPlayer() != null && FXMod.MC.interactionManager != null && this.getHeartCount(this.getRiddenEntity()) != 0 && FXMod.MC.interactionManager.hasStatusBars()) {
			return this.getHeartRows(heartCount) + 1;
		}
		else {
			return this.getHeartRows(heartCount);
		}
	}

	@Unique
	private boolean shouldCancelOverlay = false;

	@Inject(
			method = "render(Lnet/minecraft/client/gui/DrawContext;F)V",
			at = @At(
					value = "INVOKE",
					target = "net/minecraft/client/MinecraftClient.getLastFrameDuration()F"
			)
	)
	public void injectZoomOverlay(DrawContext graphics, float tickDelta, CallbackInfo ci) {
		this.shouldCancelOverlay = false;
		for (ZoomInstance instance : ZoomRegistry.getZoomInstances()) {
			ZoomOverlay overlay = instance.getZoomOverlay();
			if (overlay != null) {
				overlay.tickBeforeRender();
				if (overlay.getActive()) {
					this.shouldCancelOverlay = overlay.cancelOverlayRendering();
					overlay.renderOverlay(graphics);
				}
			}
		}
	}

	// Yes, there is a renderOverlay for being frozen...
	@Inject(
			method = {"renderSpyglassOverlay", "renderOverlay"},
			at = @At("HEAD"),
			cancellable = true
	)
	public void cancelOverlay(CallbackInfo ci) {
		if (this.shouldCancelOverlay) ci.cancel();
	}

	// ...which is why we set cancelOverlayRender to false before that!
	@Inject(
			method = "render(Lnet/minecraft/client/gui/DrawContext;F)V",
			at = @At(
					value = "INVOKE",
					target = "net/minecraft/client/network/ClientPlayerEntity.getFrozenTicks()I"
			)
	)
	public void disableOverlayCancelling(DrawContext graphics, float tickDelta, CallbackInfo ci) {
		if (this.shouldCancelOverlay) {
			this.shouldCancelOverlay = false;
		}
	}

	@Inject(method = "renderStatusEffectOverlay", at = @At("TAIL"))
	private void renderDurationOverlay(DrawContext context, CallbackInfo c) {
		if (this.client.player == null) return;
		Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
		if (!collection.isEmpty()) {
			// Replicate vanilla placement algorithm to get the duration
			// labels to line up exactly right.

			int beneficialCount = 0;
			int nonBeneficialCount = 0;
			for (StatusEffectInstance statusEffectInstance : Ordering.natural().reverse().sortedCopy(collection)) {
				StatusEffect statusEffect = statusEffectInstance.getEffectType();
				if (statusEffectInstance.shouldShowIcon()) {
					int x = this.client.getWindow().getScaledWidth();
					int y = 1;

					if (this.client.isDemo()) {
						y += 15;
					}

					if (statusEffect.isBeneficial()) {
						beneficialCount++;
						x -= 25 * beneficialCount;
					} else {
						nonBeneficialCount++;
						x -= 25 * nonBeneficialCount;
						y += 26;
					}

					String duration = getDurationAsString(statusEffectInstance);
					int durationLength = client.textRenderer.getWidth(duration);
					context.drawTextWithShadow(client.textRenderer, duration, x + 13 - (durationLength / 2), y + 14, 0x99FFFFFF);

					int amplifier = statusEffectInstance.getAmplifier();
					if (amplifier > 0) {
						// Most langages has "translations" for amplifier 1-5, converting to roman numerals
						String amplifierString = (amplifier < 6) ? I18n.translate("potion.potency." + amplifier) : "**";
						int amplifierLength = client.textRenderer.getWidth(amplifierString);
						context.drawTextWithShadow(client.textRenderer, amplifierString, x + 22 - amplifierLength, y + 3, 0x99FFFFFF);
					}
				}
			}
		}
	}

	@NotNull
	private String getDurationAsString(StatusEffectInstance statusEffectInstance) {
		int ticks = MathHelper.floor((float) statusEffectInstance.getDuration());
		int seconds = ticks / 20;

		if (ticks > 32147) {
			// Vanilla considers everything above this to be infinite
			return "**";
		} else if (seconds > 60) {
			return seconds / 60 + "m";
		} else {
			return String.valueOf(seconds);
		}
	}

	@ModifyArg(method = "renderStatusBars", index = 10, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHealthBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V"))
	public boolean renderHearts(boolean blinking) {
		return blinking && !FXMod.OPTIONS.noHeartBlink.getValue();
	}
}
