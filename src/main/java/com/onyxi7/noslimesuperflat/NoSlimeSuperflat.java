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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
    public static final String VERSION = "1.4.2";

    @Mod.Instance(MODID)
    public static NoSlimeSuperflat instance;

    @SidedProxy(clientSide = "com.onyxi7.noslimesuperflat.ClientProxy", serverSide = "com.onyxi7.noslimesuperflat.CommonProxy")
    public static CommonProxy proxy;

    public static Logger logger;
    public static Configuration config;

    // Configuration Variables - Thread-safe with volatile
    public static volatile boolean enableSlimePrevention = true;
    public static volatile boolean enableDebugLogging = false;
    public static volatile boolean blockMagmaCubes = true;
    public static volatile boolean blockOnlyUnderground = false;
    public static volatile int maxYForSpawn = 40;
    public static volatile boolean allowSurfaceSpawns = false;
    
    // Thread-safe blacklist using ConcurrentHashMap.newKeySet()
    public static final Set<String> entityBlacklist = ConcurrentHashMap.newKeySet();
    
    // Cached world type checks per dimension
    private static final ConcurrentHashMap<Integer, Boolean> superflatWorldCache = new ConcurrentHashMap<>();
    public static volatile boolean cacheWorldTypeChecks = true;

    // Atomic Statistics (Thread-safe) - Needed for commands
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

            // General Settings
            enableSlimePrevention = config.getBoolean(
                "enableSlimePrevention", 
                Configuration.CATEGORY_GENERAL, 
                true, 
                "Prevents slimes from spawning in Superflat worlds."
            );
            
            enableDebugLogging = config.getBoolean(
                "enableDebugLogging", 
                Configuration.CATEGORY_GENERAL, 
                false, 
                "Enables debug logging (may impact performance)."
            );
            
            blockMagmaCubes = config.getBoolean(
                "blockMagmaCubes", 
                Configuration.CATEGORY_GENERAL, 
                true, 
                "Also block magma cubes in Superflat worlds."
            );

            // Spawn Control Settings
            blockOnlyUnderground = config.getBoolean(
                "blockOnlyUnderground", 
                "spawn_control", 
                false, 
                "Only block slimes spawning underground (below Y=40)."
            );
            
            maxYForSpawn = config.getInt(
                "maxYForSpawn", 
                "spawn_control", 
                40, 
                0, 
                256, 
                "Maximum Y level for slime spawning when blockOnlyUnderground is enabled."
            );
            
            allowSurfaceSpawns = config.getBoolean(
                "allowSurfaceSpawns", 
                "spawn_control", 
                false, 
                "Allow slimes to spawn on the surface (above Y=60) in Superflat worlds."
            );

            // Entity Blacklist
            String[] defaultBlacklist = new String[]{"minecraft:magma_cube"};
            String[] blacklistArray = config.getStringList(
                "entityBlacklist", 
                "entities", 
                defaultBlacklist, 
                "Entities to block in Superflat worlds (format: modid:entity_name)."
            );
            
            // Clear and repopulate blacklist thread-safely
            entityBlacklist.clear();
            for (String entity : blacklistArray) {
                entityBlacklist.add(entity.toLowerCase());
            }

            // Performance Settings
            cacheWorldTypeChecks = config.getBoolean(
                "cacheWorldTypeChecks", 
                Configuration.CATEGORY_GENERAL, 
                true, 
                "Cache world type checks per dimension for better performance."
            );

            config.getCategory(Configuration.CATEGORY_GENERAL).setComment("General settings for slime prevention.");
            config.getCategory("spawn_control").setComment("Control where slimes can spawn.");
            config.getCategory("entities").setComment("Entity blacklist configuration.");

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
        clearWorldCache();
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

    // World Type Cache Methods
    public static boolean isSuperflatWorld(net.minecraft.world.World world) {
        if (world == null) return false;
        
        if (!cacheWorldTypeChecks) {
            return world.getWorldInfo().getTerrainType() == net.minecraft.world.WorldType.FLAT;
        }
        
        int dimensionId = world.provider.getDimension();
        return superflatWorldCache.computeIfAbsent(dimensionId, id -> 
            world.getWorldInfo().getTerrainType() == net.minecraft.world.WorldType.FLAT
        );
    }

    public static void clearWorldCache() {
        superflatWorldCache.clear();
        logger.debug("World type cache cleared.");
    }

    // Blacklist Helper
    public static boolean isEntityBlacklisted(String entityName) {
        if (entityName == null) return false;
        return entityBlacklist.contains(entityName.toLowerCase());
    }
}
