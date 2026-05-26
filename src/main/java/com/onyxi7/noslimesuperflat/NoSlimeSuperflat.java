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
    public static final String VERSION = "1.1.2";

    @Mod.Instance(MODID)
    public static NoSlimeSuperflat instance;

    @SidedProxy(clientSide = "com.onyxi7.noslimesuperflat.ClientProxy", serverSide = "com.onyxi7.noslimesuperflat.CommonProxy")
    public static CommonProxy proxy;

    public static Logger logger;
    public static Configuration config;

    // Configuración
    public static boolean enableDebugLogging = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = LogManager.getLogger(MODID);
        
        File configFile = event.getSuggestedConfigurationFile();
        config = new Configuration(configFile);
        syncConfig();
        
        logger.info("No Slime Superflat loaded successfully.");
        logger.info("Preventing slime spawns in Superflat worlds.");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
        logger.info("Initialization complete.");
    }

    public static void syncConfig() {
        try {
            config.load();
            
            enableDebugLogging = config.getBoolean(
                "enableDebugLogging", 
                "general", 
                false, 
                "Enable debug logging to console."
            );
            
            // Categorías
            config.getCategory("general").setComment("General settings for No Slime Superflat.");
            
        } catch (Exception e) {
            logger.error("Failed to load configuration file!", e);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}
