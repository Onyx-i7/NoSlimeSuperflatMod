package com.onyxi7.noslimesuperflat;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoSlimeSuperflat implements ModInitializer {
    public static final String MODID = "noslimesuperflat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        LOGGER.info("No Slime Superflat loaded! Slimes will be blocked in Superflat worlds.");
        FabricEventHandler.register();
    }
}
