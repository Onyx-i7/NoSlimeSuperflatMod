package com.onyxi7.noslimesuperflat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {
    // Mojang Mappings (1.17+)
    "net.minecraft.server.level.ServerLevel",
    // Fabric Yarn
    "net.minecraft.server.world.ServerWorld",
    // Fabric Intermediary (obfuscated)
    "net.minecraft.class_3218",
    // Forge MCP (1.14-1.16)
    "net.minecraft.world.server.ServerWorld",
    // Forge SRG (obfuscated)
    "net.minecraft.src.C_12_",
    // Old versions (1.8-1.12)
    "net.minecraft.world.WorldServer"
}, remap = false)
@Pseudo
public abstract class SlimeSpawnMixin {

    // For modern versions (1.17+) with Mojang mappings
    @Inject(method = {
        "addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
    }, at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void noSlimeSuperflat$blockSlimeModern(Object entity, CallbackInfoReturnable<Boolean> cir) {
        checkAndBlockSlime(entity, cir);
    }

    // For Fabric Yarn (modern)
    @Inject(method = {
        "addEntity(Lnet/minecraft/entity/Entity;)Z"
    }, at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void noSlimeSuperflat$blockSlimeYarn(Object entity, CallbackInfoReturnable<Boolean> cir) {
        checkAndBlockSlime(entity, cir);
    }

    // For Fabric Intermediary (obfuscated modern)
    @Inject(method = {
        "method_8742(Lnet/minecraft/class_1297;)Z"
    }, at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void noSlimeSuperflat$blockSlimeIntermediary(Object entity, CallbackInfoReturnable<Boolean> cir) {
        checkAndBlockSlime(entity, cir);
    }

    // For Forge SRG (obfuscated 1.14+)
    @Inject(method = {
        "func_217392_a(Lnet/minecraft/entity/Entity;)Z"
    }, at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void noSlimeSuperflat$blockSlimeSRG(Object entity, CallbackInfoReturnable<Boolean> cir) {
        checkAndBlockSlime(entity, cir);
    }

    // For old versions (1.8-1.12) - spawnEntity
    @Inject(method = {
        "spawnEntity(Lnet/minecraft/entity/Entity;)Z",
        "spawnEntity(Lnet/minecraft/entity/Entity;)Z"
    }, at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void noSlimeSuperflat$blockSlimeOldSpawn(Object entity, CallbackInfoReturnable<Boolean> cir) {
        checkAndBlockSlime(entity, cir);
    }

    // For old Forge SRG (1.8-1.12)
    @Inject(method = {
        "func_72838_d(Lnet/minecraft/entity/Entity;)Z"
    }, at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void noSlimeSuperflat$blockSlimeOldForge(Object entity, CallbackInfoReturnable<Boolean> cir) {
        checkAndBlockSlime(entity, cir);
    }

    // Shared logic for all injection points
    private void checkAndBlockSlime(Object entity, CallbackInfoReturnable<Boolean> cir) {
        try {
            // 1. Check if entity is a Slime
            String entityClass = entity.getClass().getName();
            boolean isSlime = entityClass.contains("Slime") || 
                             entityClass.contains("class_1685") ||  // Intermediary
                             entityClass.contains("C_556") ||       // SRG
                             entityClass.endsWith("EntitySlime");   // 1.8-1.12
            
            if (!isSlime) {
                return;
            }
            
            // 2. Check if world is superflat
            if (isSuperflatWorld(this)) {
                cir.setReturnValue(false);
            }
        } catch (Throwable t) {
            // Silently ignore errors
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
                    return terrainType.toString().contains("FLAT");
                } catch (Throwable t2) {
                    // Try isFlatWorld() (1.13-1.16)
                    return (Boolean) worldInfo.getClass().getMethod("isFlatWorld").invoke(worldInfo);
                }
            } catch (Throwable t3) {
                return false;
            }
        }
    }
}
