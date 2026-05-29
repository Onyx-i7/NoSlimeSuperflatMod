package com.onyxi7.noslimesuperflat.commands;

import com.onyxi7.noslimesuperflat.NoSlimeSuperflat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

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
            sender.sendMessage(new TextComponentString("§6[NoSlimeSuperflat] §eUsage: /noslimesuperflat <stats|reload>"));
            return;
        }

        String subCommand = args[0].toLowerCase();

        if ("stats".equals(subCommand)) {
            sendStats(sender);
        } else if ("reload".equals(subCommand)) {
            reloadConfig(sender);
        } else {
            sender.sendMessage(new TextComponentString("§c[NoSlimeSuperflat] Unknown command. Use: /noslimesuperflat <stats|reload>"));
        }
    }

    private void sendStats(ICommandSender sender) {
        long blockedCount = NoSlimeSuperflat.getBlockedSlimeCount();
        long checkCount = NoSlimeSuperflat.getSpawnCheckCount();
        
        sender.sendMessage(new TextComponentString("§6--- No Slime Superflat Statistics ---"));
        sender.sendMessage(new TextComponentString("§eTotal Spawn Checks: §f" + checkCount));
        sender.sendMessage(new TextComponentString("§eSlimes Blocked: §f" + blockedCount));
        
        if (checkCount > 0) {
            double efficiency = ((double) blockedCount / checkCount) * 100.0;
            sender.sendMessage(new TextComponentString(String.format("§eBlock Efficiency: §f%.2f%%", efficiency)));
        }
        
        sender.sendMessage(new TextComponentString("§eOptimized Checking: §f" + NoSlimeSuperflat.useOptimizedSpawnChecking));
        sender.sendMessage(new TextComponentString("§eWorld Caching: §f" + NoSlimeSuperflat.cacheWorldTypeChecks));
    }

    private void reloadConfig(ICommandSender sender) {
        try {
            NoSlimeSuperflat.reloadConfig();
            sender.sendMessage(new TextComponentString("§a[NoSlimeSuperflat] Configuration reloaded successfully!"));
            sender.sendMessage(new TextComponentString("§ePrevention Active: §f" + NoSlimeSuperflat.enableSlimePrevention));
            sender.sendMessage(new TextComponentString("§eBlacklist Size: §f" + NoSlimeSuperflat.entityBlacklist.size()));
        } catch (Exception e) {
            NoSlimeSuperflat.logger.error("Failed to reload config via command", e);
            sender.sendMessage(new TextComponentString("§c[NoSlimeSuperflat] Error reloading configuration. Check logs."));
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