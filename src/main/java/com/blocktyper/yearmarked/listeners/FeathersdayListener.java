package com.blocktyper.yearmarked.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.blocktyper.yearmarked.YearmarkedCalendar;
import com.blocktyper.yearmarked.DayOfWeekEnum;
import com.blocktyper.yearmarked.LocalizedMessageEnum;
import com.blocktyper.yearmarked.YearmarkedPlugin;

public class FeathersdayListener extends AbstractListener {

	public FeathersdayListener(YearmarkedPlugin plugin) {
		super(plugin);
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerFallDamageEvent(final EntityDamageEvent event) {
		YearmarkedCalendar cal = new YearmarkedCalendar(event.getEntity().getWorld());
		if (!cal.getDayOfWeekEnum().equals(DayOfWeekEnum.FEATHERSDAY)) {
			return;
		}
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();
		if (event.getCause() == DamageCause.FALL) {
			
			if (!plugin.getConfig().getBoolean(YearmarkedPlugin.CONFIG_KEY_FEATHERSDAY_PREVENT_FALL_DAMAGE, true)) {
				plugin.debugInfo(YearmarkedPlugin.CONFIG_KEY_FEATHERSDAY_PREVENT_FALL_DAMAGE + ": false");
				return;
			}
			
			if (!worldEnabled(player.getWorld().getName(), plugin.getConfig().getString(DayOfWeekEnum.FEATHERSDAY.getDisplayKey()))) {
				return;
			}
			
			String fallDamagePrevented = plugin
					.getLocalizedMessage(LocalizedMessageEnum.FALL_DAMAGE_PREVENTED.getKey());
			player.sendMessage(ChatColor.YELLOW + fallDamagePrevented);
			event.setCancelled(true);

			if (!plugin.getConfig().getBoolean(YearmarkedPlugin.CONFIG_KEY_FEATHERSDAY_BOUNCE, true)) {
				plugin.debugInfo(YearmarkedPlugin.CONFIG_KEY_FEATHERSDAY_BOUNCE + ": false");
				return;
			}
			String nameOfFishSword = plugin.getConfig().getString(YearmarkedPlugin.RECIPE_FISH_SWORD);

			if (nameOfFishSword == null) {
				plugin.debugInfo("No Fish sword defined in config");
				return;
			}

			ItemStack itemInHand = plugin.getPlayerHelper().getItemInHand(player);

			if (itemInHand == null) {
				plugin.debugInfo("Item in hand was null");
				return;
			}

			if (itemInHand.getItemMeta().getDisplayName() == null) {
				plugin.debugInfo("Item in hand has no display name");
				return;
			}

			if (!itemInHand.getItemMeta().getDisplayName().equals(nameOfFishSword)) {
				plugin.debugInfo("Item in hand is not named '" + nameOfFishSword + "'");
				return;
			}

			Double amoundToSpeedXAndZ = plugin.getConfig()
					.getDouble(YearmarkedPlugin.CONFIG_KEY_FEATHERSDAY_BOUNCE_XZ_MULTIPLIER, 2.5);

			Vector velocity = player.getVelocity();
			velocity.setY(10.0);
			velocity.setX(velocity.getX() * amoundToSpeedXAndZ);
			velocity.setZ(velocity.getZ() * amoundToSpeedXAndZ);
			player.setVelocity(velocity);
			player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);

			return;
		}
	}

}
