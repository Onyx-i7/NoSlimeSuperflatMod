package com.onyxi7.noslimesuperflat;

import net.minecraftforge.common.config.Configuration;
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
    public static final String VERSION = "1.2.0-dev4";

    @Mod.Instance(MODID)
    public static NoSlimeSuperflat instance;

    @SidedProxy(clientSide = "com.onyxi7.noslimesuperflat.ClientProxy", serverSide = "com.onyxi7.noslimesuperflat.CommonProxy")
    public static CommonProxy proxy;

    public static Logger logger;
    public static Configuration config;

    // Configuration variables
    public static boolean enableSlimePrevention = true;
    public static boolean enableDebugLogging = false;
    public static List<String> entityBlacklist = new ArrayList<>();
    public static int maxSlimesPerChunk = -1;
    public static int slimeDespawnDistance = 128;
    public static boolean reduceSlimeAI = true;
    public static int slimeUpdateFrequency = 1;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = LogManager.getLogger(MODID);
        
        File configFile = event.getSuggestedConfigurationFile();
        config = new Configuration(configFile);
        syncConfig();
        
        logger.info("No Slime Superflat v{} loaded.", VERSION);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
        logger.info("Initialization complete.");
    }

    public static void syncConfig() {
        try {
            config.load();

            enableSlimePrevention = config.getBoolean(
                "enableSlimePrevention", "general", true,
                "If true, prevents slimes from spawning in Superflat worlds."
            );

            enableDebugLogging = config.getBoolean(
                "enableDebugLogging", "general", false,
                "Enables debug logging to the console when entities are blocked."
            );

            // CORRECCIÓN 1: Inicialización explícita de la lista para compatibilidad con Java 8
            String[] defaultBlacklist = new String[]{"minecraft:magma_cube"};
            String[] currentBlacklist = config.getStringList(
                "entityBlacklist", "general", 
                defaultBlacklist, 
                "List of entity registry names to prevent from spawning in Superflat worlds (e.g., 'modid:entity_name')."
            );
            
            entityBlacklist.clear();
            for (String s : currentBlacklist) {
                if (!s.isEmpty()) {
                    entityBlacklist.add(s.trim().toLowerCase());
                }
            }

            maxSlimesPerChunk = config.getInt(
                "maxSlimesPerChunk", "performance", -1, -1, 100,
                "Maximum number of slimes allowed per chunk. -1 for unlimited."
            );

            slimeDespawnDistance = config.getInt(
                "slimeDespawnDistance", "performance", 128, 32, 512,
                "Distance in blocks after which slimes are forcibly despawned for optimization."
            );

            reduceSlimeAI = config.getBoolean(
                "reduceSlimeAI", "performance", true,
                "Reduces AI updates for slimes when no player is nearby."
            );

            slimeUpdateFrequency = config.getInt(
                "slimeUpdateFrequency", "performance", 1, 1, 20,
                "How often (in ticks) slimes update their AI. Higher values improve performance."
            );

            config.getCategory("general").setComment("General settings for No Slime Superflat");
            config.getCategory("performance").setComment("Performance optimizations. Adjust these if you experience lag.");

        } catch (Exception e) {
            logger.error("Failed to load configuration!", e);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}
