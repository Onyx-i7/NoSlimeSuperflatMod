package com.onyxi7.noslimesuperflat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityList;

@Mod.EventBusSubscriber(modid = NoSlimeSuperflat.MODID)
public class EventHandler {

    @SubscribeEvent
    public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        // EARLY EXIT #1: Check if prevention is disabled
        if (!NoSlimeSuperflat.enableSlimePrevention) {
            return;
        }

        World world = event.getWorld();
        
        // EARLY EXIT #2: Null check
        if (world == null) {
            return;
        }

        // EARLY EXIT #3: Check if world is superflat (cached for performance)
        if (!NoSlimeSuperflat.isSuperflatWorld(world)) {
            return;
        }

        Entity entity = event.getEntity();
        
        // EARLY EXIT #4: Null check
        if (entity == null) {
            return;
        }

        boolean shouldBlock = false;
        String entityName = "unknown";

        // Check if it's a slime
        if (entity instanceof EntitySlime) {
            // Check magma cubes separately if enabled
            if (entity instanceof EntityMagmaCube) {
                if (NoSlimeSuperflat.blockMagmaCubes) {
                    shouldBlock = true;
                    entityName = "magma_cube";
                }
            } else {
                // Regular slime - check spawn conditions
                shouldBlock = shouldBlockSlimeSpawn(world, entity);
                entityName = "slime";
            }
        } else {
            // Check blacklist for non-slime entities
            ResourceLocation registryName = EntityList.getKey(entity);
            if (registryName != null) {
                entityName = registryName.toString().toLowerCase();
                shouldBlock = NoSlimeSuperflat.isEntityBlacklisted(entityName);
            }
        }

        // Block the spawn if needed
        if (shouldBlock) {
            if (NoSlimeSuperflat.enableDebugLogging) {
                NoSlimeSuperflat.logger.debug("Blocked {} spawn at X:{} Y:{} Z:{} in Superflat world.", 
                    entityName, 
                    (int)entity.posX, 
                    (int)entity.posY, 
                    (int)entity.posZ
                );
            }
            
            event.setResult(Event.Result.DENY);
        }
    }

    /**
     * Determines if a slime spawn should be blocked based on configuration
     */
    private static boolean shouldBlockSlimeSpawn(World world, Entity entity) {
        int yPos = (int) entity.posY;

        // If blocking only underground
        if (NoSlimeSuperflat.blockOnlyUnderground) {
            // Block only if below maxYForSpawn
            return yPos <= NoSlimeSuperflat.maxYForSpawn;
        }

        // If allowing surface spawns
        if (NoSlimeSuperflat.allowSurfaceSpawns) {
            // Block only if below surface level (Y=60)
            return yPos < 60;
        }

        // Default: block all slimes in superflat
        return true;
    }
}
