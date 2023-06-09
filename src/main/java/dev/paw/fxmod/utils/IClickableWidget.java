package dev.paw.fxmod.utils;

import net.minecraft.client.gui.DrawContext;

public interface IClickableWidget
{
	void _renderWithoutTooltip(DrawContext context, int mouseX, int mouseY, float delta);
}

