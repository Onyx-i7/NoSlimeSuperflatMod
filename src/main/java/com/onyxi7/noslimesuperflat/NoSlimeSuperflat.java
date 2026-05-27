package com.onyxi7.noslimesuperflat;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

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
    public static final String VERSION = "1.1.3";

    @Mod.Instance(MODID)
    public static NoSlimeSuperflat instance;

    @SidedProxy(clientSide = "com.onyxi7.noslimesuperflat.ClientProxy", serverSide = "com.onyxi7.noslimesuperflat.CommonProxy")
    public static CommonProxy proxy;

    public static Logger logger;
    public static Configuration config;

    // --- General Settings ---
    public static boolean enableSlimePrevention = true;
    public static boolean enableDebugLogging = false;

    // --- Performance Optimizations ---
    public static int maxSlimesPerChunk = 0; // 0 = unlimited (but prevention handles it)
    public static int slimeDespawnDistance = 128;
    public static boolean reduceSlimeAI = true;
    public static int slimeUpdateFrequency = 20; // Tick interval for AI updates

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = LogManager.getLogger(MODID);
        
        File configFile = event.getSuggestedConfigurationFile();
        config = new Configuration(configFile);
        syncConfig();
        
        logger.info("No Slime Superflat loaded successfully.");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
        logger.info("Initialization complete.");
    }

    /**
     * Synchronizes configuration file with local variables.
     * Called on load and when GUI is closed.
     */
    public static void syncConfig() {
        try {
            config.load();
            
            // General Category
            config.addCustomCategoryComment("general", "General settings for No Slime Superflat");
            enableSlimePrevention = config.getBoolean(
                "enableSlimePrevention", "general", true, 
                "If true, prevents slimes from spawning in Superflat worlds."
            );
            enableDebugLogging = config.getBoolean(
                "enableDebugLogging", "general", false, 
                "Enables debug logging to the console when slimes are blocked."
            );

            // Performance Category
            config.addCustomCategoryComment("performance", "Performance optimizations to reduce lag");
            
            maxSlimesPerChunk = config.getInt(
                "maxSlimesPerChunk", "performance", 0, 0, 100, 
                "Maximum number of slimes allowed per chunk. 0 = unlimited (prevention handles blocking)."
            );
            
            slimeDespawnDistance = config.getInt(
                "slimeDespawnDistance", "performance", 128, 32, 256, 
                "Distance in blocks at which slimes instantly despawn to save memory."
            );
            
            reduceSlimeAI = config.getBoolean(
                "reduceSlimeAI", "performance", true, 
                "Reduces AI calculations for slimes outside player range."
            );
            
            slimeUpdateFrequency = config.getInt(
                "slimeUpdateFrequency", "performance", 20, 1, 100, 
                "Tick interval for slime AI updates. Higher = less CPU usage."
            );

        } catch (Exception e) {
            logger.error("Failed to load configuration!", e);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}
