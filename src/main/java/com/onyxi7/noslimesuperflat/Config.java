package com.onyxi7.noslimesuperflat;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Configuration handler for No Slime Superflat mod.
 * 
 * Provides runtime-configurable options through Forge's configuration system.
 * All settings can be modified via the in-game config GUI or by editing
 * the configuration file directly.
 * 
 * Configuration File Location:
 * - Windows: %APPDATA%\.minecraft\config\noslimesuperflat.cfg
 * - Linux: ~/.minecraft/config/noslimesuperflat.cfg
 * - macOS: ~/Library/Application Support/minecraft/config/noslimesuperflat.cfg
 * 
 * @author Onyx_i7
 * @version 1.1.0
 */
public class Config {
    
    /** Configuration file instance */
    private final Configuration configuration;
    
    /**
     * Enables or disables slime prevention in Superflat worlds.
     * When disabled, slimes will spawn normally in Superflat worlds.
     * 
     * Default: true
     */
    public boolean enableSlimePrevention = true;
    
    /**
     * Enables debug logging for slime spawn attempts.
     * Only recommended for troubleshooting; may impact performance.
     * 
     * Default: false
     */
    public boolean enableDebugLogging = false;
    
    /** Category names */
    private static final String CATEGORY_GENERAL = "general";
    private static final String CATEGORY_DEBUG = "debug";
    
    /**
     * Constructs a new configuration handler and loads settings from file.
     * 
     * @param configFile The configuration file path provided by FML
     */
    public Config(File configFile) {
        this.configuration = new Configuration(configFile);
        loadConfiguration();
    }
    
    /**
     * Loads configuration values from the configuration file.
     * Creates default values if the file does not exist.
     */
    private void loadConfiguration() {
        try {
            // Load general category
            configuration.addCustomCategoryComment(CATEGORY_GENERAL, "General mod settings");
            enableSlimePrevention = configuration.getBoolean(
                "enableSlimePrevention",
                CATEGORY_GENERAL,
                true,
                "When enabled, prevents slimes from spawning in Superflat worlds."
            );
            
            // Load debug category
            configuration.addCustomCategoryComment(CATEGORY_DEBUG, "Debug and troubleshooting options");
            enableDebugLogging = configuration.getBoolean(
                "enableDebugLogging",
                CATEGORY_DEBUG,
                false,
                "Logs blocked slime spawn attempts. Enable only for debugging."
            );
            
        } catch (Exception e) {
            NoSlimeSuperflat.getLogger().error("Failed to load configuration", e);
        } finally {
            // Save configuration if any changes were made
            if (configuration.hasChanged()) {
                configuration.save();
            }
        }
    }
    
    /**
     * Saves the current configuration to file.
     * Called automatically when configuration is modified.
     */
    public void save() {
        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
    
    /**
     * Retrieves the underlying Configuration object.
     * Used for synchronization with in-game config GUI.
     * 
     * @return The Forge Configuration instance
     */
    public Configuration getConfiguration() {
        return configuration;
    }
}
