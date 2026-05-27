package com.onyxi7.noslimesuperflat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public class ConfigGuiFactory implements IModGuiFactory {
    
    @Override
    public void initialize(Minecraft minecraftInstance) {
        // No special initialization is required
    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new ConfigGui(parentScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        // Return categories that support real-time updates without a restart
        // This lets Forge know that our configuration can be dynamically reloaded
        return null; // null indicates that all categories are runtime
    }
}
