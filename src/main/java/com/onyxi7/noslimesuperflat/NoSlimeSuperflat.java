package com.onyxi7.noslimesuperflat;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Mod(
    modid = NoSlimeSuperflat.MODID,
    name = NoSlimeSuperflat.NAME,
    version = NoSlimeSuperflat.VERSION,
    acceptedMinecraftVersions = "[1.12,1.13)",
    dependencies = "required-after:forge@[14.23.5.2847,)",
    guiFactory = "com.onyxi7.noslimesuperflat.ConfigGuiFactory"
)
public class NoSlimeSuperflat {

    public static final String MODID = "noslimesuperflat";
    public static final String NAME = "No Slime Superflat";
    public static final String VERSION = "1.2.0";

    @Mod.Instance(MODID)
    public static NoSlimeSuperflat instance;

    @SidedProxy(clientSide = "com.onyxi7.noslimesuperflat.ClientProxy", serverSide = "com.onyxi7.noslimesuperflat.CommonProxy")
    public static CommonProxy proxy;

    public static Logger logger;
    public static Configuration config;

    // --- Configuration Variables ---
    
    // General
    public static boolean enableSlimePrevention = true;
    public static boolean enableDebugLogging = false;
    
    // Blacklist System (v1.2.0)
    public static List<String> entityBlacklist = new ArrayList<>();
    public static boolean useBlacklist = true;

    // Performance & Optimization (v1.2.0)
    public static int maxEntitiesPerChunk = -1; // -1 disables limit
    public static double despawnDistance = 128.0; // Instant despawn if player is further
    public static boolean reduceAIOutsideRange = true;
    public static int aiUpdateFrequency = 20; // Ticks between updates

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = LogManager.getLogger(MODID);
        
        File configFile = event.getSuggestedConfigurationFile();
        config = new Configuration(configFile);
        
        try {
            syncConfig();
            logger.info("Configuration loaded successfully.");
        } catch (Exception e) {
            logger.error("Failed to load configuration!", e);
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
        logger.info("No Slime Superflat v{} initialized.", VERSION);
        if (enableDebugLogging) {
            logger.debug("Debug mode is ENABLED.");
            logger.debug("Blacklist entries: {}", entityBlacklist.size());
        }
    }

    /**
     * Synchronizes configuration file with static variables.
     * Called on startup and when GUI is closed.
     */
    public static void syncConfig() {
        if (config == null) return;

        try {
            config.load();

            // --- General Settings ---
            Property propEnable = config.get("general", "enableSlimePrevention", true);
            propEnable.setComment("If false, the mod does nothing. Main toggle.");
            enableSlimePrevention = propEnable.getBoolean();

            Property propDebug = config.get("general", "enableDebugLogging", false);
            propDebug.setComment("Enables verbose logging for troubleshooting.");
            enableDebugLogging = propDebug.getBoolean();

            // --- Blacklist System (New in 1.2.0) ---
            Property propUseBlacklist = config.get("blacklist", "useBlacklist", true);
            propUseBlacklist.setComment("If true, entities in the 'entityBlacklist' will also be prevented from spawning in Superflat worlds.");
            useBlacklist = propUseBlacklist.getBoolean();

            Property propList = config.get("blacklist", "entityBlacklist", new String[]{
                "minecraft:slime", 
                "minecraft:magma_cube"
            });
            propList.setComment("List of Entity IDs to block. Format: 'modid:entity_name'.");
            
            // Convert array to List safely
            entityBlacklist.clear();
            for (String s : propList.getStringList()) {
                if (s != null && !s.trim().isEmpty()) {
                    entityBlacklist.add(s.trim().toLowerCase());
                }
            }

            // --- Performance Settings (New in 1.2.0) ---
            Property propMax = config.get("performance", "maxEntitiesPerChunk", -1);
            propMax.setComment("Maximum allowed entities from the blacklist per chunk. -1 for unlimited.");
            maxEntitiesPerChunk = propMax.getInt();

            Property propDespawn = config.get("performance", "despawnDistance", 128.0);
            propDespawn.setComment("Distance in blocks. If a player is further than this, blocked entities are forcibly despawned instantly for performance.");
            despawnDistance = propDespawn.getDouble();

            Property propAI = config.get("performance", "reduceAIOutsideRange", true);
            propAI.setComment("If true, reduces AI tasks for blocked entities outside player render distance.");
            reduceAIOutsideRange = propAI.getBoolean();

            Property propFreq = config.get("performance", "aiUpdateFrequency", 20);
            propFreq.setComment("How often (in ticks) to check AI reduction. Higher = more performance.");
            aiUpdateFrequency = propFreq.getInt();

            // Set Category Comments
            config.getCategory("general").setComment("General toggles and logging.");
            config.getCategory("blacklist").setComment("Configure which mobs are affected by the mod.");
            config.getCategory("performance").setComment("Advanced optimization settings. Modify only if you experience lag.");

        } catch (Exception e) {
            logger.error("Critical error loading config", e);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}