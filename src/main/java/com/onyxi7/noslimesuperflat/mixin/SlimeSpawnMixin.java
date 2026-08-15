package com.onyxi7.noslimesuperflat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {
    "net.minecraft.server.level.ServerLevel",
    "net.minecraft.server.world.ServerWorld",
    "net.minecraft.class_3218",
    "net.minecraft.world.server.ServerWorld",
    "net.minecraft.src.C_12_",
    "net.minecraft.world.WorldServer"
}, remap = false)
@Pseudo
public abstract class SlimeSpawnMixin {

    // Modern versions (1.17+) with Mojang/Yarn mappings
    @Inject(method = {
        "addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z",
        "addEntity(Lnet/minecraft/entity/Entity;)Z",
        "method_8742(Lnet/minecraft/class_1297;)Z",
        "func_217392_a(Lnet/minecraft/entity/Entity;)Z"
    }, at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void noSlimeSuperflat$blockSlimeModern(net.minecraft.world.entity.Entity entity, CallbackInfoReturnable<Boolean> cir) {
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
                log("Blocking slime spawn: " + entity.getClass().getName());
                cir.setReturnValue(false);
            }
        } catch (Throwable t) {
            logError("Error in handleEntitySpawn: " + t.getMessage());
        }
    }

    private boolean shouldBlockEntity(Object entity) {
        try {
            String entityClass = entity.getClass().getName();
            log("Checking entity: " + entityClass);
            
            boolean isSlime = 
                entityClass.contains("Slime") ||
                entityClass.equals("net.minecraft.entity.monster.EntitySlime") ||
                entityClass.equals("net.minecraft.entity.monster.SlimeEntity") ||
                entityClass.equals("net.minecraft.world.entity.monster.Slime") ||
                entityClass.equals("net.minecraft.class_1685") ||
                entityClass.equals("net.minecraft.entity.mob.SlimeEntity") ||
                entityClass.contains("EntitySlime") ||
                entityClass.endsWith("Slime");
            
            if (!isSlime) {
                return false;
            }

            log("Entity is a slime, checking world type...");
            boolean isSuperflat = isSuperflatWorld(this);
            log("Is superflat world: " + isSuperflat);
            
            return isSuperflat;
            
        } catch (Throwable t) {
            logError("Error in shouldBlockEntity: " + t.getMessage());
            return false;
        }
    }
    
    private static boolean isSuperflatWorld(Object world) {
        try {
            // Try modern method (1.17+)
            Object server = world.getClass().getMethod("getServer").invoke(world);
            Object worldData = server.getClass().getMethod("getWorldData").invoke(server);
            return (Boolean) worldData.getClass().getMethod("isFlatWorld").invoke(worldData);
        } catch (Throwable t1) {
            try {
                // Try old method (1.8-1.16)
                Object worldInfo = world.getClass().getMethod("getWorldInfo").invoke(world);
                
                // Try getTerrainType() (1.8-1.12)
                try {
                    Object terrainType = worldInfo.getClass().getMethod("getTerrainType").invoke(worldInfo);
                    String typeString = terrainType.toString();
                    log("Terrain type: " + typeString);
                    return typeString.contains("FLAT") || typeString.contains("flat");
                } catch (Throwable t2) {
                    // Try isFlatWorld() (1.13-1.16)
                    try {
                        return (Boolean) worldInfo.getClass().getMethod("isFlatWorld").invoke(worldInfo);
                    } catch (Throwable t3) {
                        logError("All world detection methods failed");
                        return false;
                    }
                }
            } catch (Throwable t5) {
                logError("Failed to get WorldInfo: " + t5.getMessage());
                return false;
            }
        }
    }
    
    private static void log(String message) {
        try {
            System.out.println("[NoSlimeSuperflat] " + message);
        } catch (Throwable ignored) {}
    }
    
    private static void logError(String message) {
        try {
            System.err.println("[NoSlimeSuperflat ERROR] " + message);
        } catch (Throwable ignored) {}
    }
}
