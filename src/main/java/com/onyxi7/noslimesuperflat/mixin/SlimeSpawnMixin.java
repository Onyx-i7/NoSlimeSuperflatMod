package com.onyxi7.noslimesuperflat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {
    // Modern versions (1.17+) - Mojang mappings
    "net.minecraft.server.level.ServerLevel",
    // Fabric Yarn
    "net.minecraft.server.world.ServerWorld",
    // Fabric Intermediary
    "net.minecraft.class_3218",
    // Forge MCP (1.14-1.16)
    "net.minecraft.world.server.ServerWorld",
    // Forge SRG
    "net.minecraft.src.C_12_",
    // Old versions (1.8-1.12)
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

    // For versions where spawnEntity returns void (fallback)
    @Inject(method = {
        "spawnEntity(Lnet/minecraft/entity/Entity;)V"
    }, at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void noSlimeSuperflat$blockSlimeVoid(net.minecraft.entity.Entity entity, CallbackInfo ci) {
        handleEntitySpawnVoid(entity, ci);
    }

    private void handleEntitySpawn(Object entity, CallbackInfoReturnable<Boolean> cir) {
        try {
            if (shouldBlockEntity(entity)) {
                cir.setReturnValue(false);
            }
        } catch (Throwable t) {
            // Silently ignore errors to prevent crashes
        }
    }

    private void handleEntitySpawnVoid(Object entity, CallbackInfo ci) {
        try {
            if (shouldBlockEntity(entity)) {
                ci.cancel();
                try {
                    entity.getClass().getMethod("setDead").invoke(entity);
                } catch (Throwable ignored) {
                    try {
                        entity.getClass().getMethod("discard").invoke(entity);
                    } catch (Throwable ignored2) {
                        // Entity will still spawn, but we tried
                    }
                }
            }
        } catch (Throwable t) {
            // Silently ignore
        }
    }

    private boolean shouldBlockEntity(Object entity) {
        try {
            // Check if it's a slime by class name
            String entityClass = entity.getClass().getName();
            
            // List of all possible slime class names across versions
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

            // Check if world is superflat
            return isSuperflatWorld(this);
            
        } catch (Throwable t) {
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
                    return typeString.contains("FLAT") || typeString.contains("flat");
                } catch (Throwable t2) {
                    // Try isFlatWorld() (1.13-1.16)
                    try {
                        return (Boolean) worldInfo.getClass().getMethod("isFlatWorld").invoke(worldInfo);
                    } catch (Throwable t3) {
                        // Try getGenerator() method
                        try {
                            Object generator = world.getClass().getMethod("getChunkSource").invoke(world);
                            String generatorClass = generator.getClass().getName();
                            return generatorClass.contains("Flat") || generatorClass.contains("flat");
                        } catch (Throwable t4) {
                            return false;
                        }
                    }
                }
            } catch (Throwable t5) {
                return false;
            }
        }
    }
}
