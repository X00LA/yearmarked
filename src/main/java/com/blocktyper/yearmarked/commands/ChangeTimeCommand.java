package com.blocktyper.yearmarked.commands;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.blocktyper.yearmarked.LocalizedMessageEnum;
import com.blocktyper.yearmarked.YearmarkedPlugin;

public class ChangeTimeCommand implements CommandExecutor {

	private YearmarkedPlugin plugin;

	public ChangeTimeCommand(YearmarkedPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		try {
			if (!(sender instanceof Player)) {
				return false;
			}

			Player player = (Player) sender;
			
			if(!plugin.worldEnabled(player.getWorld().getName())){
				plugin.debugInfo("no time commands. world not enabled.");
				String message = plugin.getLocalizedMessage(LocalizedMessageEnum.WORLD_NOT_ENABLED.getKey());
				player.sendMessage(new MessageFormat(message).format(new Object[]{player.getWorld().getName()}));
				return false;
			}

			if (!player.isOp()) {
				String message = plugin.getLocalizedMessage(LocalizedMessageEnum.ONLY_FOR_OP.getKey());
				player.sendMessage(message);
				return true;
			}

			double daysToProgress = 1.0;
			boolean isRelative = false;

			if (args != null && args.length > 0) {

				String daysToProgressString = args[0];

				if (daysToProgressString != null) {
					
					if(daysToProgressString.startsWith("(") && daysToProgressString.endsWith(")")){
						isRelative = true;
						daysToProgressString = daysToProgressString.substring(1, daysToProgressString.length()-1);
					}
					
					try {
						daysToProgress = Double.parseDouble(daysToProgressString);
					} catch (Exception e) {
						String message = plugin.getLocalizedMessage(LocalizedMessageEnum.YM_COMMAND_NOT_A_VALID_NUMBER.getKey());
						sender.sendMessage(
								ChatColor.RED + new MessageFormat(message).format(new Object[]{daysToProgressString}));
						return false;
					}
					
				}
			}
			
			int ticksInDay = 24000;

			Double valueToProgress = daysToProgress * ticksInDay;
			
			if(isRelative){

				if(player.getWorld().getFullTime() < ticksInDay){
					if(valueToProgress < 0){
						valueToProgress = player.getWorld().getFullTime()*-1.0;
					}else{
						valueToProgress = valueToProgress - player.getWorld().getFullTime();
					}
				}else{
					double remainder = player.getWorld().getFullTime()%ticksInDay;
					valueToProgress = valueToProgress - remainder;
				}
			}
			
			

			Long fullTime = player.getWorld().getFullTime() + Long.valueOf(valueToProgress.longValue());
			
			if(fullTime < 0){
				fullTime = 0L;
			}

			player.getWorld().setFullTime(fullTime);

			player.sendMessage("/time set " + fullTime);

			return true;
		} catch (Exception e) {
			plugin.warning("error during change-time command");
			return false;
		}

	}

}
