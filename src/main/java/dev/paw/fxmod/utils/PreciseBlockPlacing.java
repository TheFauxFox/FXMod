package dev.paw.fxmod.utils;

import dev.paw.fxmod.mixin.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PreciseBlockPlacing {
    private static BlockPos lastTargetPos;
    private static Vec3d lastPlayerPos;
    private static Direction lastTargetSide;

    public PreciseBlockPlacing() {

    }

    public void clientTick(MinecraftClient client) {
        if (client.world == null || client.player == null) return;

        int timer = ((MinecraftClientAccessor)client).getItemUseCooldown();
        HitResult hover = client.crosshairTarget;
        if (hover != null && hover.getType() == HitResult.Type.BLOCK) {
            BlockHitResult hit = (BlockHitResult) hover;
            Direction side = hit.getSide();
            BlockPos pos = hit.getBlockPos();
            Vec3d playerPos = client.player.getPos();
            if (timer > 0) {
                if (!pos.equals(lastTargetPos) && (lastTargetPos == null || !pos.equals(lastTargetPos.offset(lastTargetSide)))) {
                    ((MinecraftClientAccessor)client).setItemUseCooldown(0);
                } else {
                    ((MinecraftClientAccessor)client).setItemUseCooldown(9999);
                }
            } else {
                BlockPos playerBlockPos = client.player.getBlockPos();
                if (side == Direction.UP && !playerPos.equals(lastPlayerPos) && playerBlockPos.getX() == pos.getX() && playerBlockPos.getZ() == pos.getZ()) {
                    ((MinecraftClientAccessor)client).setItemUseCooldown(0);
                } else {
                    ((MinecraftClientAccessor)client).setItemUseCooldown(9999);
                }
            }
            lastTargetPos = pos.toImmutable();
            lastPlayerPos = playerPos;
            lastTargetSide = side;
        }

    }
}
