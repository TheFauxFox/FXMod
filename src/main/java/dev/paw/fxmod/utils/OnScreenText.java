package dev.paw.fxmod.utils;

import dev.paw.fxmod.FXMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

public class OnScreenText
{
	public static void drawToolWarningText(DrawContext context)
	{
		// last half a second fade-out
		int alpha = MathHelper.clamp(MathHelper.ceil(25.5f * FXMod.VARS.getToolWarningTextTicksLeft()), 0, 255);

		int y = (int)(((FXMod.MC.getWindow().getScaledHeight() / 2 / 1.5)) - (FXMod.MC.textRenderer.fontHeight + 60/ 1.5));

		final String ToolWarningText = FXMod.VARS.toolHand.equals(Hand.MAIN_HAND) ? Text.translatable("fxmod.mod.toolwarning.popup.mainhand", FXMod.VARS.toolDurability).getString() : Text.translatable("fxmod.mod.toolwarning.popup.offhand", FXMod.VARS.toolDurability).getString();
		context.drawTextWithShadow(FXMod.MC.textRenderer, ToolWarningText, -(FXMod.MC.textRenderer.getWidth(ToolWarningText) / 2), y, new Color(alpha, 255, 100, 100).getPacked());
	}
}
