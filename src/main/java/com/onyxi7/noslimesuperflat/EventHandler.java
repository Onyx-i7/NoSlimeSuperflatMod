package com.onyxi7.noslimesuperflat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;
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
        // Performance Optimization: Early exit if prevention is disabled
        if (!NoSlimeSuperflat.enableSlimePrevention) {
            return;
        }

        Entity entity = event.getEntity();
        World world = event.getWorld();

        // Performance Optimization: Security Check
        if (world == null || world.getWorldInfo().getTerrainType() != WorldType.FLAT) {
            return;
        }

        boolean isSlime = entity instanceof EntitySlime;
        boolean isBlacklisted = false;
        String entityName = "unknown";

        // Get the entity name compatible with 1.12.2
        ResourceLocation registryName = EntityList.getKey(entity.getClass());
        if (registryName != null) {
            entityName = registryName.toString();
            
            // Check the blacklist if it's not a vanilla slime
            if (!isSlime) {
                String lowerName = entityName.toLowerCase();
                for (String blacklisted : NoSlimeSuperflat.entityBlacklist) {
                    if (blacklisted.toLowerCase().equals(lowerName)) {
                        isBlacklisted = true;
                        break;
                    }
                }
            }
        } else {
            // Fallback if there is no registry name
            entityName = entity.getClass().getSimpleName();
        }

        if (isSlime || isBlacklisted) {
            // Performance Optimization: Limit on the Number of Entities per Chunk
            if (NoSlimeSuperflat.maxSlimesPerChunk > 0 && (entity instanceof EntitySlime)) {
                int currentCount = world.getEntitiesWithinAABB(EntitySlime.class, entity.getEntityBoundingBox().grow(16, 16, 16)).size();
                // We multiply by 16 as an approximate safety margin for the chunk
                if (currentCount > (NoSlimeSuperflat.maxSlimesPerChunk * 16)) {
                    if (NoSlimeSuperflat.enableDebugLogging) {
                        NoSlimeSuperflat.logger.debug("Blocked {} spawn due to max count limit in chunk.", entityName);
                    }
                    event.setResult(net.minecraftforge.fml.common.eventhandler.Event.Result.DENY);
                    return;
                }
            }

            if (NoSlimeSuperflat.enableDebugLogging) {
                NoSlimeSuperflat.logger.debug("Blocked {} spawn in Superflat world at [{}, {}, {}].", 
                    entityName, Math.round(event.getX()), Math.round(event.getY()), Math.round(event.getZ()));
            }
            
            event.setResult(net.minecraftforge.fml.common.eventhandler.Event.Result.DENY);
        }
    }
}