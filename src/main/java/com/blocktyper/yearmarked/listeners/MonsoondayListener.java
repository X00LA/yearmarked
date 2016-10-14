package com.blocktyper.yearmarked.listeners;

import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.blocktyper.yearmarked.MinecraftCalendar;
import com.blocktyper.yearmarked.DayOfWeekEnum;
import com.blocktyper.yearmarked.YearmarkedPlugin;

public class MonsoondayListener extends AbstractListener {
	
	public MonsoondayListener(YearmarkedPlugin plugin) {
		super(plugin);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void playerJoin(PlayerJoinEvent event) {
		initPlayer(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void playerChangedWorld(PlayerChangedWorldEvent event) {
		initPlayer(event.getPlayer());
	}

	private void initPlayer(Player player) {
		if (!worldEnabled(player.getWorld().getName(), plugin.getConfig().getString(DayOfWeekEnum.MONSOONDAY.getDisplayKey()))) {
			return;
		}
		if (!plugin.getConfig().getBoolean(YearmarkedPlugin.CONFIG_KEY_MONSOONDAY_RAIN, true)) {
			plugin.debugInfo(YearmarkedPlugin.CONFIG_KEY_MONSOONDAY_RAIN + ": false");
			return;
		}
		MinecraftCalendar cal = new MinecraftCalendar(player.getWorld().getFullTime());
		if (DayOfWeekEnum.MONSOONDAY.equals(cal.getDayOfWeekEnum())) {
			player.setPlayerWeather(WeatherType.DOWNFALL);
		} else {
			player.setPlayerWeather(WeatherType.CLEAR);
		}
	}
	
}
