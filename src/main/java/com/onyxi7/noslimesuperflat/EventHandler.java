package com.onyxi7.noslimesuperflat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = NoSlimeSuperflat.MODID)
public class EventHandler {

    @SubscribeEvent
    public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        // Early Exit: If prevention is disabled, do nothing (Zero overhead)
        if (!NoSlimeSuperflat.enableSlimePrevention) {
            return;
        }

        Entity entity = event.getEntity();
        if (entity == null || entity.world == null) {
            return; // Safety check for null world/entity
        }

        World world = entity.world;

        // Check if world is Superflat
        if (world.getWorldInfo().getTerrainType() != WorldType.FLAT) {
            return;
        }

        String entityName = entity.getName().toLowerCase();
        boolean isTarget = false;

        // Check default Slime
        if (entity instanceof EntitySlime) {
            isTarget = true;
        }

        // Check Blacklist (New in 1.2.0)
        if (NoSlimeSuperflat.useBlacklist && !isTarget) {
            // Use resource location for accurate matching if available, fallback to name
            String registryName = entity.getRegistryName() != null ? 
                entity.getRegistryName().toString().toLowerCase() : entityName;
            
            for (String blocked : NoSlimeSuperflat.entityBlacklist) {
                if (registryName.equals(blocked) || entityName.equals(blocked)) {
                    isTarget = true;
                    break;
                }
            }
        }

        if (isTarget) {
            // Debug Logging
            if (NoSlimeSuperflat.enableDebugLogging) {
                NoSlimeSuperflat.logger.debug("Blocked {} spawn at [{}, {}, {}] in Superflat.", 
                    entityName, 
                    (int)event.getX(), (int)event.getY(), (int)event.getZ());
            }

            // Deny Spawn
            event.setResult(net.minecraftforge.fml.common.eventhandler.Event.Result.DENY);
        }
    }
}