package com.blocktyper.yearmarked.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.blocktyper.yearmarked.YearmarkedPlugin;

public class AbstractListener implements Listener {
	protected YearmarkedPlugin plugin;

	public AbstractListener(YearmarkedPlugin plugin) {
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	protected void warning(String msg) {
		plugin.getLogger().warning(msg);
	}

	protected void info(String msg) {
		plugin.getLogger().info(msg);
	}

	protected void dropItemsInStacks(Location location, Material mat, int amount, String customDisplayName) {
		// world permissions should not be checked at this low level
		if (amount > mat.getMaxStackSize()) {
			dropItemsInStacks(location, mat, mat.getMaxStackSize(), customDisplayName);
			dropItemsInStacks(location, mat, amount - mat.getMaxStackSize(), customDisplayName);

		} else {
			ItemStack item = new ItemStack(mat, amount);
			if (customDisplayName != null) {
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.setDisplayName(customDisplayName);
				item.setItemMeta(itemMeta);
			}
			location.getWorld().dropItemNaturally(location, item);
		}
	}

	protected boolean worldEnabled(String worldName, String rewardNameToLog) {
		boolean enabled = plugin.worldEnabled(worldName);
		if (!enabled && rewardNameToLog != null)
			plugin.debugInfo("No " + rewardNameToLog + " rewards enabled in the current world: " + worldName);

		return enabled;
	}

}
