package dev.paw.fxmod.mixin;

import dev.paw.fxmod.FXMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MinecraftClient.class, priority = 999)
abstract class MinecraftClientMixin {

	@Shadow
	public ClientPlayerEntity player;

	@Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
	private void onDoAttack(CallbackInfoReturnable<Boolean> info)
	{
		if(FXMod.OPTIONS.noToolBreaking.getValue() && !FXMod.INSTANCE.isToolBreakingOverriden()) {
			ItemStack mainHandItem = player.getMainHandStack();

			if(mainHandItem.isDamaged()) {
				if(mainHandItem.getItem() instanceof SwordItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.setReturnValue(false);
					}
				}
				else if(mainHandItem.getItem() instanceof TridentItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.setReturnValue(false);
					}
				}
				else if(mainHandItem.getItem() instanceof MiningToolItem){
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.setReturnValue(false);
					}
				}
			}
		}
	}

	@Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
	private void onHandleBlockBreaking(boolean bl, CallbackInfo info) {
		if(FXMod.OPTIONS.noToolBreaking.getValue() && !FXMod.INSTANCE.isToolBreakingOverriden()) {
			ItemStack mainHandItem = player.getMainHandStack();

			if(mainHandItem.isDamaged()) {
				if(mainHandItem.getItem() instanceof SwordItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof TridentItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof MiningToolItem){
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
			}
		}
	}

	@Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
	private void onDoItemUseBefore(CallbackInfo info) {
		if(FXMod.OPTIONS.noToolBreaking.getValue() && !FXMod.INSTANCE.isToolBreakingOverriden()) {
			ItemStack mainHandItem = player.getStackInHand(Hand.MAIN_HAND).isEmpty() ? null : player.getStackInHand(Hand.MAIN_HAND);
			ItemStack offHandItem = player.getStackInHand(Hand.OFF_HAND).isEmpty() ? null : player.getStackInHand(Hand.OFF_HAND);

			if(mainHandItem != null && mainHandItem.isDamaged()) {
				if(mainHandItem.getItem() instanceof MiningToolItem){
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof CrossbowItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 10) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof TridentItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof BowItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
			}

			if(offHandItem != null && offHandItem.isDamaged()) {
				if(offHandItem.getItem() instanceof MiningToolItem) {
					if(offHandItem.getMaxDamage() - offHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(offHandItem.getItem() instanceof CrossbowItem) {
					if(offHandItem.getMaxDamage() - offHandItem.getDamage() < 10) {
						info.cancel();
					}
				}
				else if(offHandItem.getItem() instanceof TridentItem) {
					if(offHandItem.getMaxDamage() - offHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(offHandItem.getItem() instanceof BowItem) {
					if(offHandItem.getMaxDamage() - offHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
			}
		}
	}
}
