package com.blocktyper.yearmarked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	
	public static int DAY_TIME = (24000/4);
	public static int NIGHT_TIME = (24000/4)*3;

	public TimeMonitor(YearmarkedPlugin plugin, String world) {
		super();
		this.plugin = plugin;
		this.world = this.plugin.getServer().getWorld(world);
	}

	// BEGIN BukkitRunnable
	public void run() {
		MinecraftCalendar cal = new MinecraftCalendar(world.getFullTime());
		checkForDayChange(cal);
		checkForPerpetualDayOrNight(cal);
		checkForConstantLightning(cal);
	}
	// END BukkitRunnable

	// BEGIN Public Utility Methods
	public void sendDayInfo(MinecraftCalendar cal, List<Player> players) {
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
		
		//if the previous day was Lightness or Twilightness, we need to reset the time to 0
		world.setTime(0L);
		
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
	
	private void checkForPerpetualDayOrNight(MinecraftCalendar cal){
		if(cal.getDayOfWeekEnum().equals(MinecraftDayOfWeekEnum.LIGHTNESS)){
			world.setTime(DAY_TIME);
		}else if(cal.getDayOfWeekEnum().equals(MinecraftDayOfWeekEnum.TWILIGHTNESS)){
			world.setTime(NIGHT_TIME);
		}
	}
	
	private void checkForConstantLightning(MinecraftCalendar cal){
		if(cal.getDayOfWeekEnum().equals(MinecraftDayOfWeekEnum.DONNERSTAG)){
			if (world.getPlayers() != null) {
				
				boolean doStrike = random.nextBoolean();
				boolean isOdd = random.nextBoolean();
				
				if(doStrike){
					int i = 1;
					for (Player player : world.getPlayers()) {
						doStrike = (isOdd && i%2 > 0) || (!isOdd && i%2 == 0);
						if(doStrike){
							Location loc = player.getLocation();
							int x = loc.getBlockX() + random.nextInt(15);
							int z = loc.getBlockX() + random.nextInt(15);
							Location newLocation = new Location(world, x, loc.getBlockY(), z);
							world.strikeLightning(newLocation);
						}
					}
				}
				
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
