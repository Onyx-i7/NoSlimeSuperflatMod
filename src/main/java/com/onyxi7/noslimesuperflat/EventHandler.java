package com.onyxi7.noslimesuperflat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityList;

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

        if (world == null) {
            return;
        }

        // Increment check count
        NoSlimeSuperflat.incrementCheckCount();

        boolean isSlime = entity instanceof EntitySlime;
        boolean isBlacklisted = false;
        String entityName = entity.getClass().getSimpleName();

        // Check blacklist for non-slime entities
        if (!isSlime) {
            ResourceLocation registryName = EntityList.getKey(entity);
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
            // Check if it's a superflat world
            if (world.getWorldInfo().getTerrainType() == WorldType.FLAT) {
                
                // Performance: Max slimes per chunk check
                if (NoSlimeSuperflat.maxSlimesPerChunk > 0 && (entity instanceof EntitySlime)) {
                    int currentCount = world.getEntitiesWithinAABB(EntitySlime.class, entity.getEntityBoundingBox().grow(16, 16, 16)).size();
                    if (currentCount > (NoSlimeSuperflat.maxSlimesPerChunk * 16)) {
                        if (NoSlimeSuperflat.enableDebugLogging) {
                            NoSlimeSuperflat.logger.debug("Blocked {} spawn due to max count limit.", entityName);
                        }
                        NoSlimeSuperflat.incrementBlockedCount();
                        event.setResult(net.minecraftforge.fml.common.eventhandler.Event.Result.DENY);
                        return;
                    }
                }

                // Block the spawn
                if (NoSlimeSuperflat.enableDebugLogging) {
                    NoSlimeSuperflat.logger.debug("Blocked {} spawn in Superflat world.", entityName);
                }
                
                NoSlimeSuperflat.incrementBlockedCount();
                event.setResult(net.minecraftforge.fml.common.eventhandler.Event.Result.DENY);
            }
        }
    }
}