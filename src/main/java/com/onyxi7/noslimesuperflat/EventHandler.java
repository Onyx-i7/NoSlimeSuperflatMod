package com.onyxi7.noslimesuperflat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = NoSlimeSuperflat.MODID)
public class EventHandler {

    @SubscribeEvent
    public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        // Early exit if prevention is disabled
        if (!NoSlimeSuperflat.enableSlimePrevention) {
            return;
        }

        Entity entity = event.getEntity();
        World world = (World) event.getWorld();

        if (world == null) {
            return;
        }

        // Increment check count
        NoSlimeSuperflat.incrementCheckCount();

        boolean isSlime = entity instanceof SlimeEntity;
        boolean isBlacklisted = false;
        String entityName = entity.getType().getRegistryName() != null ? 
            entity.getType().getRegistryName().toString() : 
            entity.getClass().getSimpleName();

        // Check blacklist for non-slime entities
        if (!isSlime) {
            ResourceLocation registryName = ForgeRegistries.ENTITIES.getKey(entity.getType());
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
            if (isSuperflatWorld(world)) {
                
                // Performance: Max slimes per chunk check
                if (NoSlimeSuperflat.maxSlimesPerChunk > 0 && (entity instanceof SlimeEntity)) {
                    AxisAlignedBB bb = entity.getBoundingBox().inflate(16, 16, 16);
                    long currentCount = world.getEntitiesOfClass(SlimeEntity.class, bb).size();
                    if (currentCount > (NoSlimeSuperflat.maxSlimesPerChunk * 16)) {
                        if (NoSlimeSuperflat.enableDebugLogging) {
                            NoSlimeSuperflat.logger.debug("Blocked {} spawn due to max count limit.", entityName);
                        }
                        NoSlimeSuperflat.incrementBlockedCount();
                        event.setResult(Event.Result.DENY);
                        return;
                    }
                }

                // Block the spawn
                if (NoSlimeSuperflat.enableDebugLogging) {
                    NoSlimeSuperflat.logger.debug("Blocked {} spawn in Superflat world.", entityName);
                }
                
                NoSlimeSuperflat.incrementBlockedCount();
                event.setResult(Event.Result.DENY);
            }
        }
    }

    // Helper method to check if world is superflat
    private static boolean isSuperflatWorld(World world) {
        if (world.isClientSide()) {
            return false;
        }
        
        if (world.getChunkSource() instanceof net.minecraft.world.server.ServerChunkProvider) {
            return ((net.minecraft.world.server.ServerChunkProvider) world.getChunkSource()).generator 
                instanceof net.minecraft.world.gen.FlatChunkGenerator;
        }
        return false;
    }
}
