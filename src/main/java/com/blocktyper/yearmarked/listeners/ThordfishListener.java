package com.blocktyper.yearmarked.listeners;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.yearmarked.DayOfWeekEnum;
import com.blocktyper.yearmarked.MinecraftCalendar;
import com.blocktyper.yearmarked.YearmarkedPlugin;

public class ThordfishListener extends AbstractListener {

	public ThordfishListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void blockDamage(BlockDamageEvent event) {

		plugin.debugInfo("BlockDamageEvent - Material " + event.getBlock().getType().name());

		if (plugin.getNameOfThordfish() == null || plugin.getNameOfThordfish().isEmpty()) {
			plugin.debugInfo("There is no recipe defined for " + YearmarkedPlugin.RECIPE_THORDFISH);
			return;
		}
		
		if (!worldEnabled(event.getPlayer().getWorld().getName(), plugin.getNameOfThordfish())) {
			return;
		}

		MinecraftCalendar cal = new MinecraftCalendar(event.getPlayer().getWorld());
		DayOfWeekEnum dayOfWeekEnum = cal.getDayOfWeekEnum();
		if (!dayOfWeekEnum.equals(DayOfWeekEnum.MONSOONDAY) && !dayOfWeekEnum.equals(DayOfWeekEnum.DONNERSTAG)) {
			plugin.debugInfo("Not " + DayOfWeekEnum.MONSOONDAY + " or " + DayOfWeekEnum.DONNERSTAG);
			return;
		}

		ItemStack itemInHand = event.getItemInHand();

		if (itemInHand == null) {
			plugin.debugWarning("Not holding an item");
			return;
		}

		if (!itemInHand.getType().equals(Material.RAW_FISH)) {
			plugin.debugWarning("Not holding a fish");
			return;
		}

		if (itemInHand.getItemMeta() == null || itemInHand.getItemMeta().getDisplayName() == null) {
			plugin.debugWarning("Not holding fish with a name.");
			return;
		}

		String itemName = itemInHand.getItemMeta().getDisplayName();

		boolean isThordfish = itemName.equals(plugin.getNameOfThordfish());

		if (!isThordfish) {
			plugin.debugInfo("Not a " + plugin.getNameOfThordfish());
			return;
		}
		
		String localizedAndTokenizedAffordMessage = plugin.getLocalizedMessage(YearmarkedPlugin.LOCALIZED_KEY_TOGGLE_EFFECT_WITH_THORDFISH_CANT_AFFORD);

		if (dayOfWeekEnum.equals(DayOfWeekEnum.MONSOONDAY)) {
			if (event.getPlayer().getPlayerWeather().equals(WeatherType.CLEAR)) {

				int toggleCost = plugin.getConfig()
						.getInt(YearmarkedPlugin.CONFIG_KEY_MONSOONDAY_RAIN_TOGGLE_ON_WITH_THORDFISH_COST, 0);

				if (spendThorfish(event.getPlayer(), itemInHand, toggleCost)) {
					if (!plugin.getConfig().getBoolean(
							YearmarkedPlugin.CONFIG_KEY_MONSOONDAY_ALLOW_RAIN_TOGGLE_WITH_THORDFISH, true)) {
						plugin.debugInfo(
								YearmarkedPlugin.CONFIG_KEY_MONSOONDAY_ALLOW_RAIN_TOGGLE_WITH_THORDFISH + ": false");
						return;
					}
					event.getPlayer().sendMessage(ChatColor.DARK_BLUE + ":(");
					event.getPlayer().setPlayerWeather(WeatherType.DOWNFALL);
				} else {
					event.getPlayer().sendMessage(
							ChatColor.RED + new MessageFormat(localizedAndTokenizedAffordMessage)
									.format(new Object[] { toggleCost + plugin.getNameOfThordfish(), }));
				}

			} else {

				int toggleCost = plugin.getConfig()
						.getInt(YearmarkedPlugin.CONFIG_KEY_MONSOONDAY_RAIN_TOGGLE_OFF_WITH_THORDFISH_COST, 0);

				if (spendThorfish(event.getPlayer(), itemInHand, toggleCost)) {
					event.getPlayer().sendMessage(ChatColor.AQUA + ":)");
					event.getPlayer().setPlayerWeather(WeatherType.CLEAR);
				} else {
					event.getPlayer().sendMessage(
							ChatColor.RED + new MessageFormat(localizedAndTokenizedAffordMessage)
									.format(new Object[] { toggleCost + plugin.getNameOfThordfish(), }));
				}
			}

		} else if (dayOfWeekEnum.equals(DayOfWeekEnum.DONNERSTAG)) {
			if (!plugin.getConfig()
					.getBoolean(YearmarkedPlugin.CONFIG_KEY_DONNERSTAG_ALLOW_LIGHTNING_TOGGLE_WITH_THORDFISH, true)) {
				plugin.debugInfo(
						YearmarkedPlugin.CONFIG_KEY_DONNERSTAG_ALLOW_LIGHTNING_TOGGLE_WITH_THORDFISH + ": false");
				return;
			}

			Set<String> playerExemptFromLightning = plugin.getPlayersExemptFromLightning();
			if (playerExemptFromLightning == null) {
				playerExemptFromLightning = new HashSet<String>();
			}
			if (playerExemptFromLightning.contains(event.getPlayer().getName())) {

				int toggleCost = plugin.getConfig()
						.getInt(YearmarkedPlugin.CONFIG_KEY_DONNERSTAG_LIGHTNING_TOGGLE_ON_WITH_THORDFISH_COST, 0);

				plugin.debugInfo("toggleCost: " + toggleCost);
				if (spendThorfish(event.getPlayer(), itemInHand, toggleCost)) {
					playerExemptFromLightning.remove(event.getPlayer().getName());
					event.getPlayer().sendMessage(ChatColor.RED + ":(");
					event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(),
							Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, 1);
				} else {
					event.getPlayer().sendMessage(
							ChatColor.RED + new MessageFormat(localizedAndTokenizedAffordMessage)
									.format(new Object[] { toggleCost + plugin.getNameOfThordfish(), }));
				}
			} else {

				int toggleCost = plugin.getConfig()
						.getInt(YearmarkedPlugin.CONFIG_KEY_DONNERSTAG_LIGHTNING_TOGGLE_OFF_WITH_THORDFISH_COST, 1);

				plugin.debugInfo("toggleCost: " + toggleCost);
				if (spendThorfish(event.getPlayer(), itemInHand, toggleCost)) {
					playerExemptFromLightning.add(event.getPlayer().getName());
					event.getPlayer().sendMessage(ChatColor.GREEN + ":)");
					event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(),
							Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, 1);
				} else {
					event.getPlayer().sendMessage(
							ChatColor.RED + new MessageFormat(localizedAndTokenizedAffordMessage)
									.format(new Object[] { toggleCost + plugin.getNameOfThordfish(), }));
				}
			}
			plugin.setPlayersExemptFromLightning(playerExemptFromLightning);
		}
	}

	private boolean spendThorfish(Player player, ItemStack itemInHand, int cost) {

		if (itemInHand.getAmount() == 0 || itemInHand.getAmount() < cost)
			return false;
		
		if(itemInHand.getAmount() == cost){
			player.getInventory().remove(itemInHand);
			return true;
		}

		itemInHand.setAmount(itemInHand.getAmount() - cost);
		
		//safety check
		if(itemInHand.getAmount() == 0){
			player.getInventory().remove(itemInHand);
		}

		return true;
	}

	

}
