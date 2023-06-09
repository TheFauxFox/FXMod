package dev.paw.fxmod.mixin;

import dev.paw.fxmod.utils.ISimpleOption;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SimpleOption.class)
abstract class OptionMixin<T> implements ISimpleOption<T>
{
    @Final
    @Shadow
    private T defaultValue;

    @Shadow
    public abstract void setValue(T value);

    @Override
    public void _setValueToDefault()
    {
        setValue(defaultValue);
    }

    @Override
    public T _getDefaultValue()
    {
        return defaultValue;
    }
}

