package com.onyxi7.noslimesuperflat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.List;

@Mod.EventBusSubscriber(modid = NoSlimeSuperflat.MODID)
public class EventHandler {

    @SubscribeEvent
    public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        // Early exit if prevention is disabled
        if (!NoSlimeSuperflat.enableSlimePrevention) {
            return;
        }

        Entity entity = event.getEntity();
        World world = event.getWorld();

        // Check if world is Superflat
        if (world == null || world.getWorldInfo().getTerrainType() != WorldType.FLAT) {
            return;
        }

        boolean isSlime = entity instanceof EntitySlime;
        boolean isBlacklisted = false;
        String entityName = entity.getClass().getSimpleName();

        // Check Blacklist
        if (!isSlime) {
            ResourceLocation registryName = getEntityRegistryName(entity);
            if (registryName != null) {
                String regNameStr = registryName.toString().toLowerCase();
                entityName = regNameStr;
                for (String blacklisted : NoSlimeSuperflat.entityBlacklist) {
                    if (blacklisted.toLowerCase().equals(regNameStr)) {
                        isBlacklisted = true;
                        break;
                    }
                }
            }
        }

        if (isSlime || isBlacklisted) {
            // Performance: Max Slimes Per Chunk Check
            if (NoSlimeSuperflat.maxSlimesPerChunk > 0 && (entity instanceof EntitySlime)) {
                int currentCount = world.getEntitiesWithinAABB(EntitySlime.class, entity.getEntityBoundingBox().grow(16, 16, 16)).size();
                // Rough estimate: 16x16 area check around spawn
                if (currentCount > (NoSlimeSuperflat.maxSlimesPerChunk * 16)) { 
                    if (NoSlimeSuperflat.enableDebugLogging) {
                        NoSlimeSuperflat.logger.debug("Blocked {} spawn due to max count limit in chunk.", entityName);
                    }
                    event.setResult(net.minecraftforge.fml.common.eventhandler.Event.Result.DENY);
                    return;
                }
            }

            // Block Spawn
            if (NoSlimeSuperflat.enableDebugLogging) {
                NoSlimeSuperflat.logger.debug("Blocked {} spawn in Superflat world at [{}, {}, {}].", 
                    entityName, Math.round(event.getX()), Math.round(event.getY()), Math.round(event.getZ()));
            }
            
            event.setResult(net.minecraftforge.fml.common.eventhandler.Event.Result.DENY);
        }
    }

    /**
     * Safely retrieves the registry name for an entity in 1.12.2.
     */
    private static ResourceLocation getEntityRegistryName(Entity entity) {
        for (EntityEntry entry : EntityRegistry.getRegistry()) {
            if (entry.getEntityClass() == entity.getClass()) {
                return entry.getRegistryName();
            }
        }
        return null;
    }
}