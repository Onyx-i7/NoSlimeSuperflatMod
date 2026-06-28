package com.onyxi7.noslimesuperflat.commands;

import com.onyxi7.noslimesuperflat.NoSlimeSuperflat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.Collections;
import java.util.List;

public class CommandNoSlimeSuperflat extends CommandBase {

    @Override
    public String getName() {
        return "noslimesuperflat";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/noslimesuperflat <stats|reload>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "[NoSlimeSuperflat] " + TextFormatting.YELLOW + "Usage: /noslimesuperflat <stats|reload>"));
            return;
        }

        String subCommand = args[0].toLowerCase();

        if ("stats".equals(subCommand)) {
            sendStats(sender);
        } else if ("reload".equals(subCommand)) {
            reloadConfig(sender);
        } else {
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "[NoSlimeSuperflat] Unknown command. Use: /noslimesuperflat <stats|reload>"));
        }
    }

    private void sendStats(ICommandSender sender) {
        long blockedCount = NoSlimeSuperflat.getBlockedSlimeCount();
        long checkCount = NoSlimeSuperflat.getSpawnCheckCount();
        
        sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "--- No Slime Superflat Statistics ---"));
        sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Total Spawn Checks: " + TextFormatting.WHITE + checkCount));
        sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Slimes Blocked: " + TextFormatting.WHITE + blockedCount));
        
        if (checkCount > 0) {
            double efficiency = ((double) blockedCount / checkCount) * 100.0;
            sender.sendMessage(new TextComponentString(String.format(TextFormatting.YELLOW + "Block Efficiency: " + TextFormatting.WHITE + "%.2f%%", efficiency)));
        }
        
        sender.sendMessage(new TextComponentString(TextFormatting.GOLD + "--- Current Configuration ---"));
        sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Prevention Active: " + TextFormatting.WHITE + NoSlimeSuperflat.enableSlimePrevention));
        sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Block Magma Cubes: " + TextFormatting.WHITE + NoSlimeSuperflat.blockMagmaCubes));
        sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "World Caching: " + TextFormatting.WHITE + NoSlimeSuperflat.cacheWorldTypeChecks));
        sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Block Only Underground: " + TextFormatting.WHITE + NoSlimeSuperflat.blockOnlyUnderground));
        
        if (NoSlimeSuperflat.blockOnlyUnderground) {
            sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Max Y for Spawn: " + TextFormatting.WHITE + NoSlimeSuperflat.maxYForSpawn));
        }
        
        sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Allow Surface Spawns: " + TextFormatting.WHITE + NoSlimeSuperflat.allowSurfaceSpawns));
        sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Blacklisted Entities: " + TextFormatting.WHITE + NoSlimeSuperflat.entityBlacklist.size()));
        
        if (NoSlimeSuperflat.enableDebugLogging) {
            sender.sendMessage(new TextComponentString(TextFormatting.AQUA + "Debug Logging: ENABLED"));
        }
    }

    private void reloadConfig(ICommandSender sender) {
        try {
            NoSlimeSuperflat.reloadConfig();
            sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "[NoSlimeSuperflat] Configuration reloaded successfully!"));
            sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Prevention Active: " + TextFormatting.WHITE + NoSlimeSuperflat.enableSlimePrevention));
            sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Blacklisted Entities: " + TextFormatting.WHITE + NoSlimeSuperflat.entityBlacklist.size()));
            
            if (NoSlimeSuperflat.blockOnlyUnderground) {
                sender.sendMessage(new TextComponentString(TextFormatting.YELLOW + "Blocking slimes below Y: " + TextFormatting.WHITE + NoSlimeSuperflat.maxYForSpawn));
            }
        } catch (Exception e) {
            NoSlimeSuperflat.logger.error("Failed to reload config via command", e);
            sender.sendMessage(new TextComponentString(TextFormatting.RED + "[NoSlimeSuperflat] Error reloading configuration. Check logs."));
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, net.minecraft.util.math.BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "stats", "reload");
        }
        return Collections.emptyList();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2; // Requires OP level 2
    }
}
