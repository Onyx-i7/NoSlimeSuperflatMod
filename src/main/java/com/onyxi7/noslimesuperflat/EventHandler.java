package com.onyxi7.noslimesuperflat;

import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.world.WorldType;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

/**
 * Event handler for No Slime Superflat mod.
 * 
 * Prevents slimes from spawning in Superflat worlds using a stateless
 * event-driven approach to ensure zero memory leaks.
 * 
 * @author Onyx_i7
 * @version 1.1.0
 * @see NoSlimeSuperflat
 */
@Mod.EventBusSubscriber(modid = NoSlimeSuperflat.MODID)
public final class EventHandler {

    /** Private constructor to prevent instantiation (stateless utility class) */
    private EventHandler() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Intercepts entity spawn events and cancels slime spawns in Superflat worlds.
     * 
     * 
     * @param event The entity join world event fired by Forge
     * 
     * @implNote This method is static and stateless to prevent memory leaks
     * @implSpec Compatible with Minecraft 1.12.2; requires patching for other versions
     */
    @SubscribeEvent
    public static void onEntityJoinWorld(final EntityJoinWorldEvent event) {
        // Early exit: Feature disabled via config
        if (!NoSlimeSuperflat.getConfig().enableSlimePrevention) {
            return;
        }

        // Early exit: Not a slime
        if (!(event.getEntity() instanceof EntitySlime)) {
            return;
        }

        // Early exit: Not a Superflat world
        if (event.getWorld().getWorldType() != WorldType.FLAT) {
            return;
        }

        // Cancel the spawn event
        event.setCanceled(true);

        // Debug logging (only visible when debug level is enabled)
        final Logger logger = NoSlimeSuperflat.getLogger();
        if (logger != null && NoSlimeSuperflat.getConfig().enableDebugLogging) {
            logger.info(
                "[DEBUG] Blocked slime spawn in Superflat world at [{}, {}, {}]",
                event.getEntity().posX,
                event.getEntity().posY,
                event.getEntity().posZ
            );
        }
    }

    /**
     * Handles configuration changes at runtime.
     * Reloads configuration when modified via in-game GUI.
     * 
     * @param event The config changed event fired by Forge
     */
    @SubscribeEvent
    public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(NoSlimeSuperflat.MODID)) {
            NoSlimeSuperflat.getConfig().save();
            NoSlimeSuperflat.getLogger().info("Configuration reloaded successfully");
            
            if (NoSlimeSuperflat.getConfig().enableDebugLogging) {
                NoSlimeSuperflat.getLogger().info("[DEBUG] Slime prevention: {}", 
                    NoSlimeSuperflat.getConfig().enableSlimePrevention ? "ENABLED" : "DISABLED");
            }
        }
    }
}
