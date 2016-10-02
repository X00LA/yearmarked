package com.blocktyper.yearmarked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class TimeMonitor extends BukkitRunnable {

	private YearmarkedPlugin plugin = null;
	private World world;

	private long previousDay = 1;

	public TimeMonitor(YearmarkedPlugin plugin, String world) {
		super();
		this.plugin = plugin;
		this.world = this.plugin.getServer().getWorld(world);
	}

	// BEGIN BukkitRunnable
	public void run() {
		MinecraftCalendar cal = new MinecraftCalendar(world.getFullTime());
		checkForDayChange(cal);
	}
	// END BukkitRunnable

	// BEGIN Public Utility Methods
	public void sendDayInfo(MinecraftCalendar cal, List<Player> players) {
		plugin.getLogger().info("SENDING DAY INFO");
		StringBuilder dayMessage = new StringBuilder(
				ChatColor.GREEN + "Today is " + cal.getDayOfWeekEnum().getDisplayName());
		StringBuilder dayOfMonthMessage = new StringBuilder(
				ChatColor.GREEN + "It is the " + getNumberWithOrdinalSuffix(cal.getDayOfMonth()) + " day ");
		dayOfMonthMessage.append(" of the " + getNumberWithOrdinalSuffix(cal.getMonthOfYear()) + " month ");
		dayOfMonthMessage.append(" of the " + getNumberWithOrdinalSuffix(cal.getYear()) + " year.");
		if (players != null && !players.isEmpty()) {
			for (Player player : players) {
				player.sendMessage(dayMessage.toString());
				player.sendMessage(dayOfMonthMessage.toString());
			}
		}
	}

	public void checkForDayChange(MinecraftCalendar cal) {
		if (cal.getDay() != previousDay) {
			changeDay(cal);
		}

	}
	// END Public Utility Methods

	// BEGIN Private Utility Methods
	private void changeDay(MinecraftCalendar cal) {
		previousDay = cal.getDay();
		sendDayInfo(cal, world.getPlayers());

		boolean isMonsoonday = cal.getDayOfWeekEnum().equals(MinecraftDayOfWeekEnum.MONSOONDAY);

		if (world.getPlayers() != null) {
			for (Player player : world.getPlayers()) {
				if (!isMonsoonday) {
					player.setPlayerWeather(WeatherType.CLEAR);
				} else {
					player.setPlayerWeather(WeatherType.DOWNFALL);
				}
			}
		}
	}

	private String getNumberWithOrdinalSuffix(int number) {
		Map<String, String> ordinalSuffixMap = new HashMap<String, String>();
		ordinalSuffixMap.put("1", "st");
		ordinalSuffixMap.put("2", "nd");
		ordinalSuffixMap.put("3", "rd");
		String numberAsString = number + "";
		String onesColumn = numberAsString.substring(numberAsString.length() - 1);
		String dayOfMonthSuffix = ordinalSuffixMap.containsKey(onesColumn) ? ordinalSuffixMap.get(onesColumn) : "th";
		return number + dayOfMonthSuffix;
	}
	// END Private Utility Methods

	// BEGIN Getters and Setters
	public World getWorld() {
		return world;
	}
	// END Getters and Setters

}
