package com.onyxi7.noslimesuperflat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityList;

import java.util.List;

@Mod.EventBusSubscriber(modid = NoSlimeSuperflat.MODID)
public class EventHandler {

    @SubscribeEvent
    public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        // Optimización: Salida temprana si la prevención está desactivada
        if (!NoSlimeSuperflat.enableSlimePrevention) {
            return;
        }

        Entity entity = event.getEntity();
        World world = event.getWorld();

        // Verificación de seguridad del mundo y tipo Superflat
        if (world == null || world.getWorldInfo().getTerrainType() != WorldType.FLAT) {
            return;
        }

        boolean isSlime = entity instanceof EntitySlime;
        boolean isBlacklisted = false;
        String entityName = entity.getClass().getSimpleName();

        // Lógica de Lista Negra compatible con 1.12.2
        // CORRECCIÓN 2: Reemplazo de EntityRegistry.getRegistry() (inexistente en 1.12.2)
        // por EntityList.getClassFromName para validar nombres de entidades
        if (!isSlime && !NoSlimeSuperflat.entityBlacklist.isEmpty()) {
            // Intentamos obtener el nombre registrado comparando con la lista
            // En 1.12.2 no hay un getRegistryName() directo en Entity base seguro sin casts
            // Iteramos sobre nuestra lista negra para ver si coincide con la clase de la entidad
            for (String blacklistedName : NoSlimeSuperflat.entityBlacklist) {
                Class<?> registeredClass = EntityList.getClassFromName(blacklistedName);
                if (registeredClass != null && registeredClass.isInstance(entity)) {
                    isBlacklisted = true;
                    entityName = blacklistedName;
                    break;
                }
            }
        }

        if (isSlime || isBlacklisted) {
            // Optimización de rendimiento: Límite de entidades por chunk
            if (NoSlimeSuperflat.maxSlimesPerChunk > 0 && (entity instanceof EntitySlime)) {
                // Contar slimes en un radio de 1 chunk (aprox 16 bloques)
                List<EntitySlime> nearbySlimes = world.getEntitiesWithinAABB(EntitySlime.class, entity.getEntityBoundingBox().grow(16, 16, 16));
                // Multiplicamos por 16 como factor de seguridad volumétrico
                if (nearbySlimes.size() > (NoSlimeSuperflat.maxSlimesPerChunk * 16)) {
                    if (NoSlimeSuperflat.enableDebugLogging) {
                        NoSlimeSuperflat.logger.debug("Blocked {} spawn due to max count limit in chunk.", entityName);
                    }
                    event.setResult(net.minecraftforge.fml.common.eventhandler.Event.Result.DENY);
                    return;
                }
            }

            if (NoSlimeSuperflat.enableDebugLogging) {
                NoSlimeSuperflat.logger.debug("Blocked {} spawn in Superflat world at [{}, {}, {}].", 
                    entityName, Math.round(event.getX()), Math.round(event.getY()), Math.round(event.getZ()));
            }
            
            event.setResult(net.minecraftforge.fml.common.eventhandler.Event.Result.DENY);
        }
    }
}