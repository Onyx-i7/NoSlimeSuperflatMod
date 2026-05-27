package com.onyxi7.noslimesuperflat;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class ConfigGui extends GuiConfig {

    public ConfigGui(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(), NoSlimeSuperflat.MODID, false, false, 
              GuiConfig.getAbridgedConfigPath(NoSlimeSuperflat.config.toString()));
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();
        
        // Add categories
        list.add(new ConfigElement(NoSlimeSuperflat.config.getCategory("general")));
        list.add(new ConfigElement(NoSlimeSuperflat.config.getCategory("performance")));
        
        return list;
    }
    
    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        // Reload config to apply changes immediately
        NoSlimeSuperflat.syncConfig();
    }
}
