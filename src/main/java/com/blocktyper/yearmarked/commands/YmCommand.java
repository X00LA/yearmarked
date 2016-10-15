package com.blocktyper.yearmarked.commands;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.blocktyper.yearmarked.LocalizedMessageEnum;
import com.blocktyper.yearmarked.YearmarkedCalendar;
import com.blocktyper.yearmarked.YearmarkedPlugin;

import net.md_5.bungee.api.ChatColor;

public class YmCommand implements CommandExecutor {

	private YearmarkedPlugin plugin;

	Map<String, Map<String, Long>> playerWorldReturnMap;

	public YmCommand(YearmarkedPlugin plugin) {
		this.plugin = plugin;
		playerWorldReturnMap = new HashMap<String, Map<String, Long>>();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// the default mode is to treat the first argument as a number or a
		// number surrounded by parenthesis
		// control flow is passed to handleAlternateFirstArgument the first
		// argument is not a number or
		// a number surrounded by parenthesis
		try {
			if (!(sender instanceof Player)) {
				return false;
			}

			Player player = (Player) sender;

			if (!plugin.worldEnabled(player.getWorld().getName())) {
				plugin.debugInfo("no time commands. world not enabled.");
				String message = plugin.getLocalizedMessage(LocalizedMessageEnum.WORLD_NOT_ENABLED.getKey());
				player.sendMessage(new MessageFormat(message).format(new Object[] { player.getWorld().getName() }));
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

				String firstArg = args[0];

				if (firstArg != null) {

					if (firstArg.startsWith("(") && firstArg.endsWith(")")) {
						isRelative = true;
						firstArg = firstArg.substring(1, firstArg.length() - 1);
					}

					try {
						daysToProgress = Double.parseDouble(firstArg);
					} catch (Exception e) {
						return handleAlternateFirstArgument(args, player);
					}

				}
			} else {
				return handleNoArgs(player);
			}

			int ticksInDay = 24000;

			Double valueToProgress = daysToProgress * ticksInDay;

			if (isRelative) {

				if (player.getWorld().getFullTime() < ticksInDay) {
					if (valueToProgress < 0) {
						valueToProgress = player.getWorld().getFullTime() * -1.0;
					} else {
						valueToProgress = valueToProgress - player.getWorld().getFullTime();
					}
				} else {
					double remainder = player.getWorld().getFullTime() % ticksInDay;
					valueToProgress = valueToProgress - remainder;
				}
			}

			Long fullTime = player.getWorld().getFullTime() + Long.valueOf(valueToProgress.longValue());

			if (fullTime < 0) {
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

	private boolean handleNoArgs(Player player) {
		YearmarkedCalendar cal = new YearmarkedCalendar(player.getWorld().getFullTime());
		plugin.sendDayInfo(cal, Arrays.asList(player));
		player.sendMessage(ChatColor.GREEN + "/ym help ");
		return true;
	}

	private boolean handleAlternateFirstArgument(String[] args, Player player) {
		if (args == null || args.length < 1 || args[0] == null) {
			plugin.debugInfo("Null or empty args");
			return false;
		}

		if (args[0].equals("day")) {
			plugin.debugInfo("'day' 1st arg");
			return handleDayArgument(args, player);
		} else if (args[0].equals("return")) {
			plugin.debugInfo("'return' 1st arg");
			return handleReturnArgument(args, player);
		} else if (args[0].equals("help")) {
			plugin.debugInfo("'help' 1st arg");
			return handleHelpArgument(player);
		}
		return false;
	}

	private boolean validateSecondArg(String[] args, Player player) {
		if (args.length < 2 || args[1] == null) {
			plugin.debugInfo("no 2nd arg");
			return false;
		}

		return true;
	}

	private boolean handleHelpArgument(Player player) {
		player.sendMessage(ChatColor.GREEN + "command examples: ");
		player.sendMessage("  - " + ChatColor.GREEN + "/ym 3 " + ChatColor.WHITE
				+ "Moves world's fulltime forward exactly 24000x3 units");
		player.sendMessage("  - " + ChatColor.GREEN + "/ym -1 " + ChatColor.WHITE
				+ "Moves world's fulltime backwards exactly 24000x1 units");
		player.sendMessage("  - " + ChatColor.GREEN + "/ym (5) " + ChatColor.WHITE
				+ "Moves world's fulltime forward to the exact start of the day 5 from *now");
		player.sendMessage("  - " + ChatColor.GREEN + "/ym (-2) " + ChatColor.WHITE
				+ "Moves world's fulltime backwards to the exact start of the day 2 *ago");
		player.sendMessage("  - " + ChatColor.GREEN + "/ym day 1 " + ChatColor.WHITE
				+ "Moves to day 1 through 7. And snapshots your current time for use by the 'return' command.");
		player.sendMessage("  - " + ChatColor.GREEN + "/ym return " + ChatColor.WHITE
				+ "Moves the world's full time to where the user was before they ran the 'ym day' command.");

		return true;
	}

	private boolean handleDayArgument(String[] args, Player player) {

		if (!validateSecondArg(args, player)) {
			player.sendMessage(ChatColor.RED + "supply a number. it must be 1-7");
			return false;
		}

		try {
			int dayNumber = Integer.parseInt(args[1]);

			if (dayNumber < 1 || dayNumber > 7) {
				player.sendMessage(ChatColor.RED + "number must be 1-7");
				return false;
			} else {
				setReturnValueForPlayer(player);
				int newFulltime = (dayNumber - 1) * 24000;
				player.sendMessage("/time set " + newFulltime);
				player.getWorld().setFullTime(newFulltime);
				return true;
			}

		} catch (NumberFormatException e) {
			player.sendMessage(ChatColor.RED + "error while parsing [" + args[1] + "] as an integer");
			plugin.debugInfo("issue parsing user input [" + args[1] + "] as integer: " + e.getMessage());
			return false;
		}
	}

	private boolean handleReturnArgument(String[] args, Player player) {
		Long returnValue = getReturnValueForPlayer(player);
		if (returnValue != null) {
			player.getWorld().setFullTime(returnValue);
			player.sendMessage("/time set " + returnValue);
			setReturnValueForPlayer(player, null);
			return true;
		}
		player.sendMessage("no return value is presently set");
		return false;
	}

	private void setReturnValueForPlayer(Player player, Long value) {
		if (!playerWorldReturnMap.containsKey(player.getName()) || playerWorldReturnMap.get(player.getName()) == null) {
			playerWorldReturnMap.put(player.getName(), new HashMap<String, Long>());
		}
		playerWorldReturnMap.get(player.getName()).put(player.getWorld().getName(), value);
	}

	private void setReturnValueForPlayer(Player player) {
		player.sendMessage(ChatColor.WHITE + "Return value.");
		setReturnValueForPlayer(player, player.getWorld().getFullTime());
	}

	private Long getReturnValueForPlayer(Player player) {
		if (!playerWorldReturnMap.containsKey(player.getName()) || playerWorldReturnMap.get(player.getName()) == null) {
			playerWorldReturnMap.put(player.getName(), new HashMap<String, Long>());
		}
		return playerWorldReturnMap.get(player.getName()).get(player.getWorld().getName());
	}

}
