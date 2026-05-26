package com.onyxi7.noslimesuperflat;

import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = NoSlimeSuperflat.MODID)
public class EventHandler {

    @SubscribeEvent
    public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        if (event.getEntity() instanceof EntitySlime) {
            World world = event.getWorld();
            if (world != null && world.getWorldInfo().getTerrainType() == WorldType.FLAT) {
                if (NoSlimeSuperflat.enableDebugLogging) {
                    NoSlimeSuperflat.logger.debug("Blocked slime spawn in Superflat world at: " + event.getX() + ", " + event.getZ());
                }
                event.setResult(net.minecraftforge.fml.common.eventhandler.Event.Result.DENY);
            }
        }
    }
}
