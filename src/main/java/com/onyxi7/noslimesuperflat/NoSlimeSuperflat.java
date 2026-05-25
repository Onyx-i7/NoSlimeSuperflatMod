package com.onyxi7.noslimesuperflat;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * No Slime Superflat - Minecraft Mod
 * 
 * A lightweight, memory-efficient alternative to Collective for preventing
 * slime spawns in Superflat worlds. Inspired by Serilum's "Superflat World No Slimes".
 * 
 * @author Onyx_i7
 * @version 1.1.0
 * @since 1.12.2
 */
@Mod(
    modid = NoSlimeSuperflat.MODID,
    name = NoSlimeSuperflat.NAME,
    version = NoSlimeSuperflat.VERSION,
    acceptedMinecraftVersions = "[1.12.2]"
)
public class NoSlimeSuperflat {
    
    /** Unique identifier for this mod */
    public static final String MODID = "noslimesuperflat";
    
    /** Display name of the mod */
    public static final String NAME = "No Slime Superflat";
    
    /** Current version following Semantic Versioning */
    public static final String VERSION = "1.1.0";
    
    /** Logger instance for mod output */
    private static Logger logger;
    
    /** Configuration instance */
    private static Config config;
    
    /**
     * Retrieves the mod's logger instance.
     * 
     * @return The configured logger instance
     */
    public static Logger getLogger() {
        return logger;
    }
    
    /**
     * Retrieves the mod's configuration instance.
     * 
     * @return The active configuration object
     */
    public static Config getConfig() {
        return config;
    }
    
    /**
     * FML Pre-Initialization Event Handler.
     * Initializes the logging system, configuration, and outputs startup information.
     * 
     * @param event The FML pre-initialization event provided by Forge
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = LogManager.getLogger(MODID);
        config = new Config(event.getSuggestedConfigurationFile());
        
        logger.info("==========================================");
        logger.info("{} v{} initialized", NAME, VERSION);
        logger.info("Preventing slime spawns in Superflat worlds");
        logger.info("Inspired by Serilum's Superflat World No Slimes");
        logger.info("Note: Redundant if using UniversalTweaks");
        logger.info("Configuration loaded from: {}", event.getSuggestedConfigurationFile().getAbsolutePath());
        logger.info("Slime prevention: {}", config.enableSlimePrevention ? "ENABLED" : "DISABLED");
        logger.info("==========================================");
    }
}
