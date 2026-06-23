package com.onyxi7.noslimesuperflat;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.level.Level;

public class FabricEventHandler {
    
    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (world.isClientSide()) {
                return;
            }

            if (!(entity instanceof Slime)) {
                return;
            }

            ServerLevel serverWorld = (ServerLevel) world;
            if (serverWorld.getServer().getWorldData().isFlatWorld()) {
                entity.discard();
            }
        });
    }
}
