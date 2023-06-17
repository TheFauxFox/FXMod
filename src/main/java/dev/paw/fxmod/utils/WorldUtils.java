package dev.paw.fxmod.utils;

import dev.paw.fxmod.FXMod;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldUtils {
    public static Set<BlockPos> getBlocksInRadius(int chunks, Class<? extends BlockEntity> blockEntityClass) {
        ClientPlayerEntity player = FXMod.MC.player;
        ClientWorld world = FXMod.MC.world;

        if (player == null || world == null) return Set.of();

        ChunkPos chunkPos = player.getChunkPos();
        int chunkX = chunkPos.x;
        int chunkZ = chunkPos.z;

        List<BlockPos> positions = new ArrayList<>();

        for (int i = chunkX - chunks; i <= chunkX + chunks; i++) {
            for (int j = chunkZ - chunks; j <= chunkZ + chunks; j++) {

                Chunk chunk = world.getChunk(i, j, ChunkStatus.FULL, false);
                if (chunk != null) {
                    positions.addAll(chunk.getBlockEntityPositions().stream().filter((blockPos -> {
                        if (blockPos.getSquaredDistance(player.getPos()) <= Math.pow(chunks * 16, 2)) {
                            BlockEntity blockEntity = chunk.getBlockEntity(blockPos);
                            if (blockEntity != null) {
                                return blockEntity.getClass() == blockEntityClass;
                            }
                        }

                        return false;
                    })).collect(Collectors.toSet()));
                }
            }
        }
        return new HashSet<>(positions);
    }
}
