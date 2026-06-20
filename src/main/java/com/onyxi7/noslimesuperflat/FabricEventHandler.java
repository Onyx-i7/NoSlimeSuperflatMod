package com.onyxi7.noslimesuperflat;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.levelgen.FlatLevelSource;

public class FabricEventHandler {
    
    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!(entity instanceof Slime slime)) {
                return;
            }

            if (world.isClientSide()) {
                return;
            }

            if (slime.isPersistenceRequired()) {
                return;
            }

            if (isSuperflatWorld(world)) {
                entity.discard();
            }
        });
    }

    private static boolean isSuperflatWorld(net.minecraft.world.level.Level world) {
        if (world.getChunkSource() instanceof ServerChunkCache chunkCache) {
            return chunkCache.getGenerator() instanceof FlatLevelSource;
        }
        return false;
    }
}
