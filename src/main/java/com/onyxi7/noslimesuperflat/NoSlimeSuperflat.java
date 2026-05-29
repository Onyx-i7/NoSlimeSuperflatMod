package com.onyxi7.noslimesuperflat;

import com.onyxi7.noslimesuperflat.commands.CommandNoSlimeSuperflat;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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
    public static final String VERSION = "1.2.1";

    @Mod.Instance(MODID)
    public static NoSlimeSuperflat instance;

    @SidedProxy(clientSide = "com.onyxi7.noslimesuperflat.ClientProxy", serverSide = "com.onyxi7.noslimesuperflat.CommonProxy")
    public static CommonProxy proxy;

    public static Logger logger;
    public static Configuration config;

    // Configuration Variables
    public static boolean enableSlimePrevention = true;
    public static boolean enableDebugLogging = false;
    public static List<String> entityBlacklist = new ArrayList<>();
    public static int maxSlimesPerChunk = -1;
    public static int slimeDespawnDistance = 128;
    public static boolean reduceSlimeAI = true;
    public static int slimeUpdateFrequency = 1;
    
    // Optimization Flags
    public static boolean useOptimizedSpawnChecking = true;
    public static boolean cacheWorldTypeChecks = true;

    // Atomic Statistics (Thread-safe)
    private static final AtomicLong blockedSlimeCount = new AtomicLong(0);
    private static final AtomicLong spawnCheckCount = new AtomicLong(0);

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

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandNoSlimeSuperflat());
        logger.info("Command /noslimesuperflat registered.");
    }

    public static void syncConfig() {
        try {
            config.load();

            enableSlimePrevention = config.getBoolean("enableSlimePrevention", "general", true, "Prevents slimes from spawning in Superflat worlds.");
            enableDebugLogging = config.getBoolean("enableDebugLogging", "general", false, "Enables debug logging.");
            
            entityBlacklist = new ArrayList<>();
            Collections.addAll(entityBlacklist, config.getStringList("entityBlacklist", "general", new String[]{"minecraft:magma_cube"}, "Entities to block."));

            maxSlimesPerChunk = config.getInt("maxSlimesPerChunk", "performance", -1, -1, 100, "Max slimes per chunk.");
            slimeDespawnDistance = config.getInt("slimeDespawnDistance", "performance", 128, 32, 512, "Despawn distance.");
            reduceSlimeAI = config.getBoolean("reduceSlimeAI", "performance", true, "Reduce AI updates.");
            slimeUpdateFrequency = config.getInt("slimeUpdateFrequency", "performance", 1, 1, 20, "AI update frequency.");
            
            useOptimizedSpawnChecking = config.getBoolean("useOptimizedSpawnChecking", "performance", true, "Use optimized checking logic.");
            cacheWorldTypeChecks = config.getBoolean("cacheWorldTypeChecks", "performance", true, "Cache world type checks.");

            config.getCategory("general").setComment("General settings.");
            config.getCategory("performance").setComment("Performance optimizations.");

        } catch (Exception e) {
            logger.error("Failed to load configuration!", e);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    public static void reloadConfig() {
        syncConfig();
        resetStatistics(); // Optional: reset stats on reload if desired, or keep them
        logger.info("Configuration reloaded via command.");
    }

    // Statistics Methods
    public static void incrementBlockedCount() {
        blockedSlimeCount.incrementAndGet();
    }

    public static void incrementCheckCount() {
        spawnCheckCount.incrementAndGet();
    }

    public static long getBlockedSlimeCount() {
        return blockedSlimeCount.get();
    }

    public static long getSpawnCheckCount() {
        return spawnCheckCount.get();
    }

    public static void resetStatistics() {
        blockedSlimeCount.set(0);
        spawnCheckCount.set(0);
        logger.info("Statistics reset.");
    }
    
    // Helper for imports if needed elsewhere
    public static boolean isSuperflatWorld(net.minecraft.world.World world) {
        return world.getWorldInfo().getTerrainType() == net.minecraft.world.WorldType.FLAT;
    }
}