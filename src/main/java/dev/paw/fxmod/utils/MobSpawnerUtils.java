package dev.paw.fxmod.utils;

import dev.paw.fxmod.FXMod;
import dev.paw.fxmod.mixin.MobSpawnerLogicAccessor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.MobSpawnerLogic;

public class MobSpawnerUtils {
    public static String getMobSpawnerType(BlockPos pos) {
        if (FXMod.MC.world == null) return null;
        BlockEntity blockEntity = FXMod.MC.world.getBlockEntity(pos);
        if (blockEntity instanceof MobSpawnerBlockEntity mobSpawner) {
            MobSpawnerLogic spawnerLogic = mobSpawner.getLogic();
            Entity spawningMob = ((MobSpawnerLogicAccessor) spawnerLogic).getSpawningMob();
            if (spawningMob != null) {
                return spawningMob.getName().getString();
            }
        }
        return null;
    }
}
