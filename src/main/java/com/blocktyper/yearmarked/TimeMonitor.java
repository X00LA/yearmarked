package com.blocktyper.yearmarked;

import java.text.MessageFormat;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class TimeMonitor extends BukkitRunnable {

	private YearmarkedPlugin plugin = null;
	private World world;

	private Random random = new Random();

	private long previousDay = 1;

	public static int DAY_TIME = (24000 / 4);
	public static int NIGHT_TIME = (24000 / 4) * 3;

	public TimeMonitor(YearmarkedPlugin plugin, String world) {
		super();
		this.plugin = plugin;
		this.world = this.plugin.getServer().getWorld(world);
	}

	// BEGIN BukkitRunnable
	public void run() {

		if (plugin.getConfig().getBoolean("debug")) {
			plugin.getLogger().info(this.getWorld().getName() + "[fulltime]: " + this.getWorld().getFullTime());
		}

		MinecraftCalendar cal = new MinecraftCalendar(world.getFullTime());
		checkForDayChange(cal);
		checkForConstantLightning(cal);
	}
	// END BukkitRunnable

	// BEGIN Public Utility Methods
	public void sendDayInfo(MinecraftCalendar cal, List<Player> players) {
		

		String todayIs =  String.format(plugin.getLocalizedMessage(YearmarkedPlugin.LOCALIZED_KEY_TODAY_IS), cal.getDayOfWeekEnum().getDisplayName());
		String dayOfMonthMessage = new MessageFormat(plugin.getLocalizedMessage(YearmarkedPlugin.LOCALIZED_KEY_IT_IS_DAY_NUMBER)).format(new Object[]{cal.getDayOfMonth()+"", cal.getMonthOfYear()+"", cal.getYear()+""});
		

		if (players != null && !players.isEmpty()) {
			for (Player player : players) {
				player.sendMessage(ChatColor.YELLOW + todayIs);
				player.sendMessage(dayOfMonthMessage);
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

	private void checkForConstantLightning(MinecraftCalendar cal) {
		if (!cal.getDayOfWeekEnum().equals(MinecraftDayOfWeekEnum.DONNERSTAG)) {
			return;
		}
		if (world.getPlayers() == null) {
			return;
		}
		boolean strinkeForOddPlayers = random.nextBoolean();
		int i = 0;
		for (Player player : world.getPlayers()) {
			i++;
			boolean doStrike = (strinkeForOddPlayers && i % 2 > 0) || (!strinkeForOddPlayers && i % 2 == 0);
			if (doStrike) {
				Location loc = player.getLocation();
				int x = loc.getBlockX() + random.nextInt(15) * (random.nextBoolean() ? -1 : 1);
				int z = loc.getBlockZ() + random.nextInt(15) * (random.nextBoolean() ? -1 : 1);
				Location newLocation = new Location(world, x, loc.getBlockY(), z);
				world.strikeLightning(newLocation);
			}
		}
	}
	// END Private Utility Methods

	// BEGIN Getters and Setters
	public World getWorld() {
		return world;
	}
	// END Getters and Setters

}
