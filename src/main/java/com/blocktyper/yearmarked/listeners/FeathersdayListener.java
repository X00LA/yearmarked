package com.blocktyper.yearmarked.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.blocktyper.yearmarked.MinecraftCalendar;
import com.blocktyper.yearmarked.MinecraftDayOfWeekEnum;
import com.blocktyper.yearmarked.YearmarkedPlugin;

public class FeathersdayListener extends AbstractListener {

	public FeathersdayListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerFallDamageEvent(final EntityDamageEvent event) {
		MinecraftCalendar cal = new MinecraftCalendar(event.getEntity().getWorld());
		if (!cal.getDayOfWeekEnum().equals(MinecraftDayOfWeekEnum.FEATHERSDAY)) {
			return;
		}
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();
		if (event.getCause() == DamageCause.FALL) {
			String fallDamagePrevented = plugin.getLocalizedMessage(YearmarkedPlugin.LOCALIZED_KEY_BONUS);
			player.sendMessage(ChatColor.YELLOW + fallDamagePrevented);
			event.setCancelled(true);
			return;
		}
	}

}
