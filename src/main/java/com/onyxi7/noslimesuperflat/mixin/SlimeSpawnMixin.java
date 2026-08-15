package com.onyxi7.noslimesuperflat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = {
    // Official Names (Mojang Mappings - 1.17+)
    "net.minecraft.server.level.ServerLevel",
    // Fabric Yarn
    "net.minecraft.server.world.ServerWorld",
    // Fabric Intermediary (obfuscado)
    "net.minecraft.class_3218",
    // Forge MCP (1.14-1.16)
    "net.minecraft.world.server.ServerWorld",
    // Forge SRG (obfuscado)
    "net.minecraft.src.C_12_",
    // Older versions (1.8-1.12)
    "net.minecraft.world.WorldServer"
}, remap = false)
@Pseudo
public abstract class SlimeSpawnMixin {

    @Inject(method = {
        // Mojang Mappings (1.17+)
        "addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z",
        // Fabric Yarn
        "addEntity(Lnet/minecraft/entity/Entity;)Z",
        // Fabric Intermediary
        "method_8742(Lnet/minecraft/class_1297;)Z",
        // Forge MCP (1.14-1.16)
        "addEntity(Lnet/minecraft/entity/Entity;)Z",
        // Forge SRG
        "func_217392_a(Lnet/minecraft/entity/Entity;)Z",
        // Older versions (1.8-1.12)
        "spawnEntity(Lnet/minecraft/entity/Entity;)Z",
        "func_72838_d(Lnet/minecraft/entity/Entity;)Z"
    }, at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void noSlimeSuperflat$blockSlime(Object entity, CallbackInfoReturnable<Boolean> cir) {
        try {
            String entityClass = entity.getClass().getName();
            boolean isSlime = entityClass.contains("Slime") || 
                             entityClass.contains("class_1685") ||  // Intermediary
                             entityClass.contains("C_556") ||       // SRG
                             entityClass.endsWith("EntitySlime");   // 1.8-1.12
            
            if (!isSlime) {
                return;
            }
            
            //
            if (isSuperflatWorld(this)) {
                cir.setReturnValue(false);
            }
        } catch (Throwable t) {
            //
        }
    }
    
    private static boolean isSuperflatWorld(Object world) {
        try {
            // (1.17+)
            Object server = world.getClass().getMethod("getServer").invoke(world);
            Object worldData = server.getClass().getMethod("getWorldData").invoke(server);
            return (Boolean) worldData.getClass().getMethod("isFlatWorld").invoke(worldData);
        } catch (Throwable t1) {
            try {
                // (1.8-1.16)
                Object worldInfo = world.getClass().getMethod("getWorldInfo").invoke(world);
                
                // (1.8-1.12)
                try {
                    Object terrainType = worldInfo.getClass().getMethod("getTerrainType").invoke(worldInfo);
                    return terrainType.toString().contains("FLAT");
                } catch (Throwable t2) {
                    // (1.13-1.16)
                    return (Boolean) worldInfo.getClass().getMethod("isFlatWorld").invoke(worldInfo);
                }
            } catch (Throwable t3) {
                return false;
            }
        }
    }
}
