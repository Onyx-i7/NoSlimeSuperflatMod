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
            "No Slime Superflat v" + NoSlimeSuperflat.VERSION);
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();
        
        list.add(new ConfigElement(NoSlimeSuperflat.config.getCategory("general")));
        list.add(new ConfigElement(NoSlimeSuperflat.config.getCategory("spawn_control")));
        list.add(new ConfigElement(NoSlimeSuperflat.config.getCategory("entities")));
        
        return list;
    }
}
