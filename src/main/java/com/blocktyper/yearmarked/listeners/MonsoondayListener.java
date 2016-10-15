package com.blocktyper.yearmarked.listeners;

import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.blocktyper.yearmarked.YearmarkedCalendar;
import com.blocktyper.yearmarked.ConfigKeyEnum;
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
		if (!plugin.getConfig().getBoolean(ConfigKeyEnum.MONSOONDAY_RAIN.getKey(), true)) {
			plugin.debugInfo(ConfigKeyEnum.MONSOONDAY_RAIN.getKey() + ": false");
			return;
		}
		YearmarkedCalendar cal = new YearmarkedCalendar(player.getWorld().getFullTime());
		if (DayOfWeekEnum.MONSOONDAY.equals(cal.getDayOfWeekEnum())) {
			player.setPlayerWeather(WeatherType.DOWNFALL);
		} else {
			player.setPlayerWeather(WeatherType.CLEAR);
		}
	}
	
}
