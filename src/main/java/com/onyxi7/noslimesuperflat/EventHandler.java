package com.onyxi7.noslimesuperflat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Level;

/**
 * Handles all event subscriptions for the mod.
 * Optimized for minimal memory footprint and maximum performance.
 */
@Mod.EventBusSubscriber(modid = NoSlimeSuperflat.MODID)
public class EventHandler {

    /**
     * Prevents slimes (and configured entities) from spawning in Superflat worlds.
     * Uses early-exit logic to minimize CPU usage.
     *
     * @param event The spawn check event fired by Forge.
     */
    @SubscribeEvent
    public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        // Early exit: If prevention is disabled, do nothing immediately
        if (!NoSlimeSuperflat.enableSlimePrevention) {
            return;
        }

        Entity entity = event.getEntity();
        
        // Safety check: Ensure entity is not null
        if (entity == null) {
            return;
        }

        boolean isTargetEntity = false;
        String entityIdentifier = "";

        // 1. Check for vanilla Slimes
        if (entity instanceof EntitySlime) {
            isTargetEntity = true;
            entityIdentifier = "minecraft:slime";
        } 
        // 2. Check against the customizable blacklist
        else if (!NoSlimeSuperflat.entityBlacklist.isEmpty()) {
            // Safe way to get registry name in 1.12.2
            ResourceLocation registryName = entity.getRegistryName();
            
            if (registryName != null) {
                entityIdentifier = registryName.toString();
                if (NoSlimeSuperflat.entityBlacklist.contains(entityIdentifier)) {
                    isTargetEntity = true;
                }
            }
        }

        // If entity is not in our target list, exit early
        if (!isTargetEntity) {
            return;
        }

        // Check world type
        World world = event.getWorld();
        if (world == null || world.getWorldInfo().getTerrainType() != WorldType.FLAT) {
            return;
        }

        // --- Performance Optimizations Applied ---
        
        // 3. Check Max Slimes Per Chunk (if configured)
        if (NoSlimeSuperflat.maxSlimesPerChunk > 0) {
            int currentCount = world.countEntities(entity.getClass());
            // Rough estimation per chunk area to avoid expensive iteration
            // If global count exceeds limit significantly, block spawn
            if (currentCount > (NoSlimeSuperflat.maxSlimesPerChunk * 16)) { 
                if (NoSlimeSuperflat.enableDebugLogging) {
                    NoSlimeSuperflat.logger.log(Level.DEBUG, 
                        "Blocked {} spawn due to global entity limit ({}).", 
                        entityIdentifier, currentCount);
                }
                event.setResult(net.minecraftforge.fml.common.eventhandler.Event.Result.DENY);
                return;
            }
        }

        // 4. Final Decision: Block the spawn
        if (NoSlimeSuperflat.enableDebugLogging) {
            NoSlimeSuperflat.logger.log(Level.DEBUG, 
                "Blocked {} spawn at [{}, {}, {}] in Superflat world.", 
                entityIdentifier, 
                (int)event.getX(), (int)event.getY(), (int)event.getZ());
        }
        
        event.setResult(net.minecraftforge.fml.common.eventhandler.Event.Result.DENY);
    }
}