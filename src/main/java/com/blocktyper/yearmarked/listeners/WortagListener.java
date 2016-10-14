package com.blocktyper.yearmarked.listeners;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.Crops;

import com.blocktyper.yearmarked.DayOfWeekEnum;
import com.blocktyper.yearmarked.MinecraftCalendar;
import com.blocktyper.yearmarked.YearmarkedPlugin;

public class WortagListener extends AbstractListener {

	private Random random = new Random();

	public WortagListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onCropsBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();

		MinecraftCalendar cal = new MinecraftCalendar(block.getWorld());
		if (!cal.getDayOfWeekEnum().equals(DayOfWeekEnum.WORTAG)) {
			return;
		}

		if (block.getType() != Material.NETHER_WARTS) {
			return;
		}

		if (((Crops) block.getState().getData()).getState() != CropState.RIPE) {
			return;
		}

		if (!worldEnabled(event.getPlayer().getWorld().getName(), DayOfWeekEnum.WORTAG.getDisplayKey())) {
			return;
		}
		
		int high = plugin.getConfig().getInt(YearmarkedPlugin.CONFIG_KEY_WORTAG_BONUS_CROPS_RANGE_HIGH, 3);
		int low = plugin.getConfig().getInt(YearmarkedPlugin.CONFIG_KEY_WORTAG_BONUS_CROPS_RANGE_LOW, 1);

		int rewardCount = random.nextInt(high + 1);

		if (rewardCount < low) {
			rewardCount = low;
		}

		if (rewardCount > 0) {
			String bonus = plugin.getLocalizedMessage(YearmarkedPlugin.LOCALIZED_KEY_BONUS);
			event.getPlayer().sendMessage(
					ChatColor.DARK_PURPLE + bonus + "[x" + rewardCount + "] " + block.getType().toString());
			dropItemsInStacks(block.getLocation(), Material.NETHER_WARTS, rewardCount,
					plugin.getConfig().getString(YearmarkedPlugin.CONFIG_KEY_EARTHDAY) + " "
							+ Material.NETHER_WARTS.name());
		} else {
			plugin.debugInfo("No luck on Wortag");
			event.getPlayer().sendMessage(ChatColor.RED + ":(");
		}

	}

}
