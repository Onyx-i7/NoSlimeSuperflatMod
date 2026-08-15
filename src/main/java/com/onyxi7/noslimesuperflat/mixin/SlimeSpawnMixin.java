package com.onyxi7.noslimesuperflat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Mixin(targets = {
    "net.minecraft.server.level.ServerLevel",
    "net.minecraft.server.world.ServerWorld",
    "net.minecraft.class_3218",
    "net.minecraft.world.server.ServerWorld",
    "net.minecraft.src.C_12_",
    "net.minecraft.world.WorldServer",
    "net.minecraft.world.World"
}, remap = false)
@Pseudo
public abstract class SlimeSpawnMixin {

    // Modern versions (1.17+) - Mojang mappings
    @Inject(method = {
        "addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
    }, at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void noSlimeSuperflat$blockSlimeModern(net.minecraft.world.entity.Entity entity, CallbackInfoReturnable<Boolean> cir) {
        handleEntitySpawn(entity, cir);
    }

    // Fabric Yarn
    @Inject(method = {
        "addEntity(Lnet/minecraft/entity/Entity;)Z"
    }, at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void noSlimeSuperflat$blockSlimeYarn(net.minecraft.entity.Entity entity, CallbackInfoReturnable<Boolean> cir) {
        handleEntitySpawn(entity, cir);
    }

    // Fabric Intermediary
    @Inject(method = {
        "method_8742(Lnet/minecraft/class_1297;)Z"
    }, at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void noSlimeSuperflat$blockSlimeIntermediary(net.minecraft.entity.Entity entity, CallbackInfoReturnable<Boolean> cir) {
        handleEntitySpawn(entity, cir);
    }

    // Old versions (1.8-1.12) - spawnEntity returns boolean
    @Inject(method = {
        "spawnEntity(Lnet/minecraft/entity/Entity;)Z",
        "func_72838_d(Lnet/minecraft/entity/Entity;)Z"
    }, at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void noSlimeSuperflat$blockSlimeOld(net.minecraft.entity.Entity entity, CallbackInfoReturnable<Boolean> cir) {
        handleEntitySpawn(entity, cir);
    }

    private void handleEntitySpawn(Object entity, CallbackInfoReturnable<Boolean> cir) {
        try {
            if (shouldBlockEntity(entity)) {
                System.out.println("[NoSlimeSuperflat] ✓ Blocked slime spawn in superflat world!");
                cir.setReturnValue(false);
            }
        } catch (Throwable t) {
            // Silently ignore
        }
    }

    private boolean shouldBlockEntity(Object entity) {
        try {
            String entityClass = entity.getClass().getName();
            
            boolean isSlime = 
                entityClass.contains("Slime") ||
                entityClass.equals("net.minecraft.entity.monster.EntitySlime") ||
                entityClass.equals("net.minecraft.entity.monster.SlimeEntity") ||
                entityClass.equals("net.minecraft.world.entity.monster.Slime") ||
                entityClass.equals("net.minecraft.class_1685") ||
                entityClass.equals("net.minecraft.entity.mob.SlimeEntity") ||
                entityClass.endsWith("EntitySlime");
            
            if (!isSlime) {
                return false;
            }

            System.out.println("[NoSlimeSuperflat] Detected slime: " + entityClass);
            boolean isSuperflat = isSuperflatWorld(this);
            System.out.println("[NoSlimeSuperflat] Is superflat: " + isSuperflat);
            
            return isSuperflat;
            
        } catch (Throwable t) {
            System.err.println("[NoSlimeSuperflat ERROR] shouldBlockEntity: " + t.getMessage());
            return false;
        }
    }
    
    private static boolean isSuperflatWorld(Object world) {
        try {
            // Try modern method (1.17+)
            try {
                Method getServer = findMethod(world.getClass(), "getServer");
                if (getServer != null) {
                    Object server = getServer.invoke(world);
                    Method getWorldData = findMethod(server.getClass(), "getWorldData");
                    if (getWorldData != null) {
                        Object worldData = getWorldData.invoke(server);
                        Method isFlatWorld = findMethod(worldData.getClass(), "isFlatWorld");
                        if (isFlatWorld != null) {
                            return (Boolean) isFlatWorld.invoke(worldData);
                        }
                    }
                }
            } catch (Throwable ignored) {}

            // Try Forge 1.12.2 method - getWorldInfo() or field_72986_A
            Object worldInfo = getWorldInfoObject(world);
            if (worldInfo != null) {
                System.out.println("[NoSlimeSuperflat] Found WorldInfo: " + worldInfo.getClass().getName());
                
                // Try getTerrainType (MCP) or func_76067_t (SRG)
                Object terrainType = null;
                Method getTerrainType = findMethod(worldInfo.getClass(), "getTerrainType", "func_76067_t");
                if (getTerrainType != null) {
                    terrainType = getTerrainType.invoke(worldInfo);
                    System.out.println("[NoSlimeSuperflat] TerrainType: " + terrainType);
                }

                if (terrainType != null) {
                    // Check against WorldType.FLAT
                    try {
                        Class<?> worldTypeClass = Class.forName("net.minecraft.world.WorldType");
                        Field flatField = null;
                        try { flatField = worldTypeClass.getField("FLAT"); } catch (NoSuchFieldException ignored) {}
                        
                        if (flatField != null) {
                            Object flatType = flatField.get(null);
                            if (terrainType.equals(flatType)) {
                                System.out.println("[NoSlimeSuperflat] Matched WorldType.FLAT!");
                                return true;
                            }
                        }
                    } catch (Throwable ignored) {}

                    // Check name
                    Method getName = findMethod(terrainType.getClass(), "getName", "func_77127_a", "name");
                    if (getName != null) {
                        String name = (String) getName.invoke(terrainType);
                        System.out.println("[NoSlimeSuperflat] WorldType name: " + name);
                        if (name != null && name.equalsIgnoreCase("flat")) return true;
                    }
                    
                    if (terrainType.toString().toLowerCase().contains("flat")) {
                        System.out.println("[NoSlimeSuperflat] Detected FLAT in toString()");
                        return true;
                    }
                }
            }

            // Fallback: Generator check
            try {
                Method getChunkSource = findMethod(world.getClass(), "getChunkSource", "func_72863_F");
                if (getChunkSource != null) {
                    Object chunkSource = getChunkSource.invoke(world);
                    Method getGenerator = findMethod(chunkSource.getClass(), "getGenerator");
                    if (getGenerator != null) {
                        Object generator = getGenerator.invoke(chunkSource);
                        String genClass = generator.getClass().getName();
                        if (genClass.contains("Flat") || genClass.contains("flat")) {
                            System.out.println("[NoSlimeSuperflat] Detected Flat generator: " + genClass);
                            return true;
                        }
                    }
                }
            } catch (Throwable ignored) {}

            return false;
        } catch (Throwable t) {
            System.err.println("[NoSlimeSuperflat ERROR] isSuperflatWorld: " + t.getMessage());
            t.printStackTrace();
            return false;
        }
    }

    private static Object getWorldInfoObject(Object world) throws Exception {
        // Try methods (MCP and SRG names)
        Method getWorldInfo = findMethod(world.getClass(), "getWorldInfo", "func_72912_H");
        if (getWorldInfo != null) {
            return getWorldInfo.invoke(world);
        }

        // Try fields (MCP and SRG names)
        Class<?> clazz = world.getClass();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getName().equals("worldInfo") || 
                    field.getName().equals("field_72986_A") || 
                    field.getType().getName().contains("WorldInfo")) {
                    field.setAccessible(true);
                    return field.get(world);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    private static Method findMethod(Class<?> clazz, String... names) {
        Class<?> current = clazz;
        while (current != null) {
            for (Method m : current.getDeclaredMethods()) {
                for (String name : names) {
                    if (m.getName().equals(name) && m.getParameterCount() == 0) {
                        m.setAccessible(true);
                        return m;
                    }
                }
            }
            current = current.getSuperclass();
        }
        return null;
    }
}
