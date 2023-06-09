package dev.paw.fxmod.utils;

public interface IKeyBinding
{
	void _registerKeyDownListener(_KeyDownListener listener);
	void _registerKeyUpListener(_KeyUpListener listener);

	@FunctionalInterface
	interface _KeyDownListener
	{
		void keyDownListener();
	}

	@FunctionalInterface
	interface _KeyUpListener
	{
		void keyUpListener();
	}
}
