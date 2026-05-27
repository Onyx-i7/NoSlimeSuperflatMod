package com.onyxi7.noslimesuperflat;

import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Handles all event subscriptions for the mod.
 * Optimized for zero memory leaks and maximum performance.
 */
@Mod.EventBusSubscriber(modid = NoSlimeSuperflat.MODID)
public class EventHandler {

    /**
     * Prevents slimes from spawning in Superflat worlds.
     * Includes real-time configuration support.
     */
    @SubscribeEvent
    public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        // Early exit if prevention is disabled
        if (!NoSlimeSuperflat.enableSlimePrevention) {
            return;
        }

        // Check if entity is a slime
        if (event.getEntity() instanceof EntitySlime) {
            World world = event.getWorld();
            
            // Check if world is Superflat
            if (world != null && world.getWorldInfo().getTerrainType() == WorldType.FLAT) {
                // Debug logging
                if (NoSlimeSuperflat.enableDebugLogging) {
                    NoSlimeSuperflat.logger.debug(
                        "Blocked slime spawn at [{}, {}, {}] in Superflat world.", 
                        Math.round(event.getX()), 
                        Math.round(event.getY()), 
                        Math.round(event.getZ())
                    );
                }
                
                // Deny spawn
                event.setResult(net.minecraftforge.fml.common.eventhandler.Event.Result.DENY);
            }
        }
    }
}
