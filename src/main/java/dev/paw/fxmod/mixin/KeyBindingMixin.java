package dev.paw.fxmod.mixin;

import dev.paw.fxmod.utils.IKeyBinding;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(KeyBinding.class)
abstract class KeyBindingMixin implements IKeyBinding
{
    @Shadow
    private boolean pressed;

    @Final
    @Shadow
    private static Map<InputUtil.Key, KeyBinding> KEY_TO_BINDINGS;

    private final List<_KeyDownListener> keyDownListeners = new ArrayList<>();
    private final List<_KeyUpListener> keyUpListeners = new ArrayList<>();

    private void _onKeyDownEvent()
    {
        for(_KeyDownListener l : keyDownListeners) {
            l.keyDownListener();
        }
    }

    private void _onKeyUpEvent()
    {
        for(_KeyUpListener l : keyUpListeners) {
            l.keyUpListener();
        }
    }

    @Override
    public void _registerKeyDownListener(_KeyDownListener listener)
    {
        keyDownListeners.add(listener);
    }

    @Override
    public void _registerKeyUpListener(_KeyUpListener listener)
    {
        keyUpListeners.add(listener);
    }

    @Inject(method = "setKeyPressed", at = @At("HEAD"))
    private static void onSetKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo info)
    {
        KeyBinding keyBinding = KEY_TO_BINDINGS.get(key);

        if(keyBinding != null) {
            if(pressed && !keyBinding.isPressed()) {
                ((KeyBindingMixin)(Object)keyBinding)._onKeyDownEvent();
            }
            else if(!pressed) {
                ((KeyBindingMixin)(Object)keyBinding)._onKeyUpEvent();
            }
        }
    }
}
