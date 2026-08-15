package com.onyxi7.noslimesuperflat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

    // Modern versions (1.17+) - uses net.minecraft.world.entity.Entity
    @Inject(method = {
        "addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z",
        "addEntity(Lnet/minecraft/entity/Entity;)Z",
        "method_8742(Lnet/minecraft/class_1297;)Z",
        "func_217392_a(Lnet/minecraft/entity/Entity;)Z"
    }, at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void noSlimeSuperflat$blockSlimeModern(net.minecraft.world.entity.Entity entity, CallbackInfoReturnable<Boolean> cir) {
        checkAndBlockSlime(entity, cir);
    }

    // Old versions (1.8-1.12) - uses net.minecraft.entity.Entity
    @Inject(method = {
        "spawnEntity(Lnet/minecraft/entity/Entity;)Z",
        "func_72838_d(Lnet/minecraft/entity/Entity;)Z"
    }, at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void noSlimeSuperflat$blockSlimeOld(net.minecraft.entity.Entity entity, CallbackInfoReturnable<Boolean> cir) {
        checkAndBlockSlime(entity, cir);
    }

    private void checkAndBlockSlime(Object entity, CallbackInfoReturnable<Boolean> cir) {
        try {
            String entityClass = entity.getClass().getName();
            boolean isSlime = entityClass.contains("Slime") || 
                             entityClass.contains("class_1685") ||
                             entityClass.contains("C_556") ||
                             entityClass.endsWith("EntitySlime");
            
            if (!isSlime) {
                return;
            }
            
            if (isSuperflatWorld(this)) {
                cir.setReturnValue(false);
            }
        } catch (Throwable t) {
            // Silently ignore
        }
    }
    
    private static boolean isSuperflatWorld(Object world) {
        try {
            Object server = world.getClass().getMethod("getServer").invoke(world);
            Object worldData = server.getClass().getMethod("getWorldData").invoke(server);
            return (Boolean) worldData.getClass().getMethod("isFlatWorld").invoke(worldData);
        } catch (Throwable t1) {
            try {
                Object worldInfo = world.getClass().getMethod("getWorldInfo").invoke(world);
                try {
                    Object terrainType = worldInfo.getClass().getMethod("getTerrainType").invoke(worldInfo);
                    return terrainType.toString().contains("FLAT");
                } catch (Throwable t2) {
                    return (Boolean) worldInfo.getClass().getMethod("isFlatWorld").invoke(worldInfo);
                }
            } catch (Throwable t3) {
                return false;
            }
        }
    }
}
