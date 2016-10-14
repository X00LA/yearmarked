package com.blocktyper.yearmarked.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

			if (!player.isOp()) {
				player.sendMessage("Only for OP users.");
				return true;
			}

			double daysToProgress = 1.0;

			if (args != null && args.length > 0) {

				String daysToProgressString = args[0];

				if (daysToProgressString != null) {
					try {
						daysToProgress = Double.parseDouble(daysToProgressString);
					} catch (Exception e) {
						sender.sendMessage(
								ChatColor.RED + daysToProgressString + " was not recognized as a valid number.");
						return false;
					}
				}
			}

			Double valueToProgress = daysToProgress * 24000;

			Long fullTime = Long.valueOf(valueToProgress.longValue());

			player.getWorld().setFullTime(player.getWorld().getFullTime() + fullTime);

			player.sendMessage("time progressed " + daysToProgress + " days.");

			return true;
		} catch (Exception e) {
			plugin.warning("error during change-time command");
			return false;
		}

	}

}
