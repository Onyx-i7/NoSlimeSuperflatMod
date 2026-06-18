package com.onyxi7.noslimesuperflat.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.onyxi7.noslimesuperflat.NoSlimeSuperflat;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CommandNoSlimeSuperflat {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
            LiteralArgumentBuilder.<CommandSource>literal("noslimesuperflat")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("stats")
                    .executes(context -> {
                        sendStats(context.getSource());
                        return 1;
                    })
                )
                .then(Commands.literal("reload")
                    .executes(context -> {
                        reloadConfig(context.getSource());
                        return 1;
                    })
                )
                .executes(context -> {
                    context.getSource().sendSuccess(
                        new StringTextComponent(TextFormatting.GOLD + "[NoSlimeSuperflat] " + 
                            TextFormatting.YELLOW + "Usage: /noslimesuperflat <stats|reload>"), 
                        false
                    );
                    return 1;
                })
        );
    }

    private static void sendStats(CommandSource source) {
        long blockedCount = NoSlimeSuperflat.getBlockedSlimeCount();
        long checkCount = NoSlimeSuperflat.getSpawnCheckCount();
        
        source.sendSuccess(new StringTextComponent(TextFormatting.GOLD + "--- No Slime Superflat Statistics ---"), false);
        source.sendSuccess(new StringTextComponent(TextFormatting.YELLOW + "Total Spawn Checks: " + TextFormatting.WHITE + checkCount), false);
        source.sendSuccess(new StringTextComponent(TextFormatting.YELLOW + "Slimes Blocked: " + TextFormatting.WHITE + blockedCount), false);
        
        if (checkCount > 0) {
            double efficiency = ((double) blockedCount / checkCount) * 100.0;
            source.sendSuccess(new StringTextComponent(String.format(TextFormatting.YELLOW + "Block Efficiency: " + TextFormatting.WHITE + "%.2f%%", efficiency)), false);
        }
        
        source.sendSuccess(new StringTextComponent(TextFormatting.YELLOW + "Optimized Checking: " + TextFormatting.WHITE + NoSlimeSuperflat.useOptimizedSpawnChecking), false);
        source.sendSuccess(new StringTextComponent(TextFormatting.YELLOW + "World Caching: " + TextFormatting.WHITE + NoSlimeSuperflat.cacheWorldTypeChecks), false);
    }

    private static void reloadConfig(CommandSource source) {
        try {
            NoSlimeSuperflat.reloadConfig();
            source.sendSuccess(new StringTextComponent(TextFormatting.GREEN + "[NoSlimeSuperflat] Configuration reloaded successfully!"), false);
            source.sendSuccess(new StringTextComponent(TextFormatting.YELLOW + "Prevention Active: " + TextFormatting.WHITE + NoSlimeSuperflat.enableSlimePrevention), false);
            source.sendSuccess(new StringTextComponent(TextFormatting.YELLOW + "Blacklist Size: " + TextFormatting.WHITE + NoSlimeSuperflat.entityBlacklist.size()), false);
        } catch (Exception e) {
            NoSlimeSuperflat.logger.error("Failed to reload config via command", e);
            source.sendSuccess(new StringTextComponent(TextFormatting.RED + "[NoSlimeSuperflat] Error reloading configuration. Check logs."), false);
        }
    }
}
