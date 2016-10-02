package com.blocktyper.yearmarked.listeners;

import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.blocktyper.yearmarked.MinecraftCalendar;
import com.blocktyper.yearmarked.MinecraftDayOfWeekEnum;
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
		MinecraftCalendar cal = new MinecraftCalendar(player.getWorld().getFullTime());
		if (MinecraftDayOfWeekEnum.MONSOONDAY.equals(cal.getDayOfWeekEnum())) {
			player.setPlayerWeather(WeatherType.DOWNFALL);
		} else {
			player.setPlayerWeather(WeatherType.CLEAR);
		}
	}
	
}
