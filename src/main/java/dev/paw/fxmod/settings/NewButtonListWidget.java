package dev.paw.fxmod.settings;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import dev.paw.fxmod.FXMod;
import dev.paw.fxmod.utils.Color;
import dev.paw.fxmod.utils.IClickableWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Map;

public class NewButtonListWidget extends ElementListWidget<NewButtonListWidget.NewButtonEntry>
{
	public NewButtonListWidget(MinecraftClient mc, int width, int height, int top, int bottom, int entryHeight)
	{
		super(mc, width, height, top, bottom, entryHeight);
	}

	public void addCategoryEntry(String key)
	{
		this.addEntry(NewButtonEntry.create(width, Text.translatable(key).formatted(Formatting.BOLD)));
	}

	public void addOptionEntry(SimpleOption<?> first)
	{
		this.addEntry(NewButtonEntry.create(this.width, first));
	}

	public void addOptionEntry(SimpleOption<?> first, SimpleOption<?> second)
	{
		this.addEntry(NewButtonEntry.create(this.width, first, second));
	}

	public void addOptionEntry(SimpleOption<?>[] options)
	{
		this.addEntry(NewButtonEntry.create(this.width, options));
	}

	@Override
	public int getRowWidth()
	{
		return 400;
	}

	@Override
	protected int getScrollbarPositionX()
	{
		return super.getScrollbarPositionX() + 32;
	}

	public int getBottom()
	{
		return this.bottom;
	}

	// basically a button row in this list widget (or a category)
	final static class NewButtonEntry extends Entry<NewButtonEntry>
	{
		private final List<ClickableWidget> buttons;

		private NewButtonEntry(Map<SimpleOption<?>, ClickableWidget> optionsToButtons)
		{
			this.buttons = ImmutableList.copyOf(optionsToButtons.values());
		}

		private NewButtonEntry(int width, Text category)
		{
			this.buttons = ImmutableList.of(new NewCategoryWidget(width / 2 - 155, 0, 310, 20, category));
		}

		// category
		public static NewButtonEntry create(int width, Text category)
		{
			return new NewButtonEntry(width, category);
		}

		// single button row
		public static NewButtonEntry create(int width, SimpleOption<?> option)
		{
			return new NewButtonEntry(ImmutableMap.of(option, option.createWidget(FXMod.MC.options, width / 2 - 155, 0, 310)));
		}

		// two buttons next to each other, kept for simplicity so that the dynamic create does not have to be used for every row
		public static NewButtonEntry create(int width, SimpleOption<?> firstOption, SimpleOption<?> secondOption)
		{
			return new NewButtonEntry(ImmutableMap.of(firstOption, firstOption.createWidget(FXMod.MC.options, width / 2 - 155, 0, 150), secondOption, secondOption.createWidget(FXMod.MC.options, width / 2 - 155 + 160, 0, 150)));
		}

		// dynamically makes a button entry based on how many options are given, null values can be used to make empty spaces
		public static NewButtonEntry create(int width, SimpleOption<?>[] options)
		{
			final ImmutableMap.Builder<SimpleOption<?>, ClickableWidget> builder = ImmutableMap.builder();

			float buttonWidth = (310.0f - 10.0f*(options.length - 1)) / (float)options.length;
			float pixelAdjustment = options.length * (buttonWidth - MathHelper.floor(buttonWidth));

			for(int i = 0; i < options.length; i++) {
				SimpleOption<?> option = options[i];

				if(option != null) {
					int adjustment = pixelAdjustment > 0.5f && i == options.length - 1 ? Math.round(pixelAdjustment) : 0;

					builder.put(option, option.createWidget(FXMod.MC.options, width / 2 - 155 + (i * (MathHelper.floor(buttonWidth) + 10)) + adjustment, 0, MathHelper.floor(buttonWidth)));
				}
			}

			return new NewButtonEntry(builder.build());
		}

		public List<? extends Element> children()
		{
			return this.buttons;
		}

		public List<? extends Selectable> selectableChildren()
		{
			return this.buttons;
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			this.buttons.forEach((button) -> {
				button.setY(y);

				if(FXMod.VARS.settingsShowTooltips) {
					button.render(context, mouseX, mouseY, tickDelta);
				}
				else {
					// HACK: Noone can now override the render method, but eh, it's just for this menu anyway, right?
					((IClickableWidget)button)._renderWithoutTooltip(context, mouseX, mouseY, tickDelta);
				}
			});
		}
	}

	final static class NewCategoryWidget extends ClickableWidget
	{
		public NewCategoryWidget(int x, int y, int width, int height, Text message)
		{
			super(x, y, width, height, message);
		}

		@Override
		public void renderButton(DrawContext context, int mouseX, int mouseY, float delta)
		{
			context.drawCenteredTextWithShadow(FXMod.MC.textRenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, Color.WHITE.getPacked());
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button)
		{
			return false;
		}

		@Override
		protected void appendClickableNarrations(NarrationMessageBuilder builder)
		{
			this.appendDefaultNarrations(builder);
		}
	}
}
