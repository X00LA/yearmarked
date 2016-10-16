package com.blocktyper.yearmarked;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class TimeMonitor extends BukkitRunnable {

	private YearmarkedPlugin plugin = null;
	private World world;

	private Random random = new Random();

	private long previousDay = 1;

	public static int DAY_TIME = (YearmarkedCalendar.TICKS_IN_A_DAY / 4);
	public static int NIGHT_TIME = (YearmarkedCalendar.TICKS_IN_A_DAY / 4) * 3;

	public TimeMonitor(YearmarkedPlugin plugin, String world) {
		super();
		this.plugin = plugin;
		this.world = this.plugin.getServer().getWorld(world);
	}

	// BEGIN BukkitRunnable
	public void run() {
		if (!plugin.worldEnabled(world.getName())) {
			plugin.debugInfo("no time monitor. world not enabled.");
			return;
		}

		plugin.debugInfo(this.getWorld().getName() + "[fulltime]: " + this.getWorld().getFullTime());

		YearmarkedCalendar cal = new YearmarkedCalendar(world.getFullTime());
		checkForDayChange(cal);
		checkForConstantLightning(cal);
	}
	// END BukkitRunnable

	public void checkForDayChange(YearmarkedCalendar cal) {
		if (cal.getDay() != previousDay) {
			changeDay(cal);
		}

	}
	// END Public Utility Methods

	// BEGIN Private Utility Methods
	private void changeDay(YearmarkedCalendar cal) {

		previousDay = cal.getDay();
		plugin.sendDayInfo(cal, world.getPlayers());
		plugin.setPlayersExemptFromLightning(new HashSet<String>());

		if (plugin.getConfig().getBoolean(ConfigKeyEnum.MONSOONDAY_RAIN.getKey(), true)) {
			boolean isMonsoonday = cal.getDayOfWeekEnum().equals(DayOfWeekEnum.MONSOONDAY);
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
	}

	private void checkForConstantLightning(YearmarkedCalendar cal) {
		if (!cal.getDayOfWeekEnum().equals(DayOfWeekEnum.DONNERSTAG)) {
			return;
		}
		if (world.getPlayers() == null) {
			return;
		}
		boolean strikeForOddPlayers = random.nextBoolean();
		int i = 0;
		for (Player player : world.getPlayers()) {
			i++;
			if (plugin.getPlayersExemptFromLightning() == null
					|| !plugin.getPlayersExemptFromLightning().contains(player.getName())) {
				boolean doStrike = (strikeForOddPlayers && i % 2 > 0) || (!strikeForOddPlayers && i % 2 == 0);
				if (doStrike) {
					Location loc = player.getLocation();
					int x = loc.getBlockX() + random.nextInt(15) * (random.nextBoolean() ? -1 : 1);
					int z = loc.getBlockZ() + random.nextInt(15) * (random.nextBoolean() ? -1 : 1);
					Location newLocation = new Location(world, x, loc.getBlockY()+2, z);
					world.strikeLightning(newLocation);

					if (!plugin.getConfig().getBoolean(
							ConfigKeyEnum.DONNERSTAG_ALLOW_SUPER_CREEPER_SPAWN_WITH_FISH_SWORD.getKey(), true)) {
						plugin.debugInfo(ConfigKeyEnum.DONNERSTAG_ALLOW_SUPER_CREEPER_SPAWN_WITH_FISH_SWORD.getKey()
								+ ": false");
						return;
					}

					double creeperSpawnPercentChance = plugin.getConfig().getDouble(
							ConfigKeyEnum.DONNERSTAG_SUPER_CREEPER_SPAWN_WITH_FISH_SWORD_PERCENT_CHANCE.getKey(), 100);

					boolean spawnCreeper = plugin.rollIsLucky(creeperSpawnPercentChance);
					if (!spawnCreeper) {
						plugin.debugInfo("no super creeper spawns due to good luck");
						return;
					}
					
					ItemStack itemInHand = plugin.getPlayerHelper().getItemInHand(player);
					
					
					//spawn a creeper if they are holding the fish sword or a bow with Fishbone arrows active
					if(itemInHand.getType().equals(Material.BOW)){
						
						ItemStack firstArrowStack = plugin.getPlayerHelper().getFirstArrowStack(player);

						if (firstArrowStack != null) {
							plugin.debugInfo("arrow stack located. size: " + firstArrowStack.getAmount());

							if (firstArrowStack.getItemMeta() == null || firstArrowStack.getItemMeta().getDisplayName() == null) {
								plugin.debugInfo("arrows have no display name");
								continue;
							}
							
						} else {
							plugin.debugInfo("no arrows found");
							continue;
						}
						
					}else{
						
						if (itemInHand == null || itemInHand.getItemMeta() == null
								|| itemInHand.getItemMeta().getDisplayName() == null) {
							plugin.debugInfo("Player does not have a named item in hand during lightning strike.");
							continue;
						}
						
						if (itemInHand.getItemMeta() == null
								|| itemInHand.getItemMeta().getDisplayName() == null) {
							plugin.debugInfo("Player does not have a named item in hand during lightning strike.");
							continue;
						}

						if (!itemInHand.getItemMeta().getDisplayName().equals(plugin.getNameOfFishSword())) {
							plugin.debugInfo("Player does not have an item named '" + plugin.getNameOfFishSword()
									+ "' in hand during lightning strike.");
							continue;
						}
					}

					player.sendMessage(ChatColor.RED + "Creeper!");
					new SuperCreeperDelaySpawner(plugin, world, newLocation).runTaskLater(plugin, 20L * 1);

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
