package com.onyxi7.noslimesuperflat;

import com.onyxi7.noslimesuperflat.commands.CommandNoSlimeSuperflat;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Mod(NoSlimeSuperflat.MODID)
public class NoSlimeSuperflat {

    public static final String MODID = "noslimesuperflat";
    public static final String NAME = "No Slime Superflat";
    public static final String VERSION = "1.3.0";

    public static final Logger logger = LogManager.getLogger(MODID);

    // Config Spec
    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec.BooleanValue ENABLE_SLIME_PREVENTION;
    public static ForgeConfigSpec.BooleanValue ENABLE_DEBUG_LOGGING;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> ENTITY_BLACKLIST;
    public static ForgeConfigSpec.IntValue MAX_SLIMES_PER_CHUNK;
    public static ForgeConfigSpec.IntValue SLIME_DESPAWN_DISTANCE;
    public static ForgeConfigSpec.BooleanValue REDUCE_SLIME_AI;
    public static ForgeConfigSpec.IntValue SLIME_UPDATE_FREQUENCY;
    public static ForgeConfigSpec.BooleanValue USE_OPTIMIZED_SPAWN_CHECKING;
    public static ForgeConfigSpec.BooleanValue CACHE_WORLD_TYPE_CHECKS;

    // Configuration Variables (loaded from config)
    public static boolean enableSlimePrevention = true;
    public static boolean enableDebugLogging = false;
    public static List<String> entityBlacklist = new ArrayList<>();
    public static int maxSlimesPerChunk = -1;
    public static int slimeDespawnDistance = 128;
    public static boolean reduceSlimeAI = true;
    public static int slimeUpdateFrequency = 1;
    public static boolean useOptimizedSpawnChecking = true;
    public static boolean cacheWorldTypeChecks = true;

    // Atomic Statistics (Thread-safe)
    private static final AtomicLong blockedSlimeCount = new AtomicLong(0);
    private static final AtomicLong spawnCheckCount = new AtomicLong(0);

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

        COMMON_BUILDER.comment("General settings").push("general");
        ENABLE_SLIME_PREVENTION = COMMON_BUILDER
            .comment("Prevents slimes from spawning in Superflat worlds.")
            .define("enableSlimePrevention", true);
        ENABLE_DEBUG_LOGGING = COMMON_BUILDER
            .comment("Enables debug logging.")
            .define("enableDebugLogging", false);
        ENTITY_BLACKLIST = COMMON_BUILDER
            .comment("Entities to block.")
            .defineList("entityBlacklist", 
                () -> java.util.Arrays.asList("minecraft:magma_cube"),
                obj -> obj instanceof String);
        COMMON_BUILDER.pop();

        COMMON_BUILDER.comment("Performance optimizations").push("performance");
        MAX_SLIMES_PER_CHUNK = COMMON_BUILDER
            .comment("Max slimes per chunk.")
            .defineInRange("maxSlimesPerChunk", -1, -1, 100);
        SLIME_DESPAWN_DISTANCE = COMMON_BUILDER
            .comment("Despawn distance.")
            .defineInRange("slimeDespawnDistance", 128, 32, 512);
        REDUCE_SLIME_AI = COMMON_BUILDER
            .comment("Reduce AI updates.")
            .define("reduceSlimeAI", true);
        SLIME_UPDATE_FREQUENCY = COMMON_BUILDER
            .comment("AI update frequency.")
            .defineInRange("slimeUpdateFrequency", 1, 1, 20);
        USE_OPTIMIZED_SPAWN_CHECKING = COMMON_BUILDER
            .comment("Use optimized checking logic.")
            .define("useOptimizedSpawnChecking", true);
        CACHE_WORLD_TYPE_CHECKS = COMMON_BUILDER
            .comment("Cache world type checks.")
            .define("cacheWorldTypeChecks", true);
        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public NoSlimeSuperflat() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);

        // Register mod lifecycle events (Mod Bus)
        modEventBus.addListener(this::commonSetup);
        
        // Register game events (Forge Bus)
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);

        logger.info("No Slime Superflat v{} loaded.", VERSION);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        loadConfigValues();
        logger.info("Initialization complete.");
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        CommandNoSlimeSuperflat.register(event.getDispatcher());
        logger.info("Command /noslimesuperflat registered.");
    }

    public static void loadConfigValues() {
        enableSlimePrevention = ENABLE_SLIME_PREVENTION.get();
        enableDebugLogging = ENABLE_DEBUG_LOGGING.get();
        entityBlacklist = new ArrayList<>(ENTITY_BLACKLIST.get());
        maxSlimesPerChunk = MAX_SLIMES_PER_CHUNK.get();
        slimeDespawnDistance = SLIME_DESPAWN_DISTANCE.get();
        reduceSlimeAI = REDUCE_SLIME_AI.get();
        slimeUpdateFrequency = SLIME_UPDATE_FREQUENCY.get();
        useOptimizedSpawnChecking = USE_OPTIMIZED_SPAWN_CHECKING.get();
        cacheWorldTypeChecks = CACHE_WORLD_TYPE_CHECKS.get();
    }

    public static void reloadConfig() {
        loadConfigValues();
        resetStatistics();
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

    // Helper for checking if world is superflat
    public static boolean isSuperflatWorld(net.minecraft.world.World world) {
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
