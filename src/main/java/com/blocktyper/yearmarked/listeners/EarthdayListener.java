package com.blocktyper.yearmarked.listeners;

import java.util.Random;

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

import net.md_5.bungee.api.ChatColor;

public class EarthdayListener extends AbstractListener {

	private Random random = new Random();

	public EarthdayListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onCropsBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();

		if (block.getType() != Material.CROPS && block.getType() != Material.CARROT
				&& block.getType() != Material.POTATO) {
			return;
		}

		MinecraftCalendar cal = new MinecraftCalendar(block.getWorld());
		if (!cal.getDayOfWeekEnum().equals(DayOfWeekEnum.EARTHDAY)) {
			return;
		}

		if (!plugin.getConfig().getBoolean(YearmarkedPlugin.CONFIG_KEY_EARTHDAY_BONUS_CROPS, true)) {
			plugin.debugInfo(YearmarkedPlugin.CONFIG_KEY_EARTHDAY_BONUS_CROPS + ": false");
			return;
		}

		if (block.getType() != Material.CROPS && block.getType() != Material.CARROT
				&& block.getType() != Material.POTATO) {
			return;
		}

		if (((Crops) block.getState().getData()).getState() != CropState.RIPE) {
			plugin.debugInfo("Ripe " + block.getType() + " on earthday.");
			return;
		}

		if (!worldEnabled(block.getWorld().getName(), plugin.getConfig().getString(DayOfWeekEnum.EARTHDAY.getDisplayKey()))) {
			return;
		}
		

		int high = plugin.getConfig().getInt(YearmarkedPlugin.CONFIG_KEY_EARTHDAY_BONUS_CROPS_RANGE_HIGH, 3);
		int low = plugin.getConfig().getInt(YearmarkedPlugin.CONFIG_KEY_EARTHDAY_BONUS_CROPS_RANGE_LOW, 1);

		int rewardCount = random.nextInt(high + 1);

		if (rewardCount < low) {
			rewardCount = low;
		}

		if (rewardCount > 0) {
			String bonus = plugin.getLocalizedMessage(YearmarkedPlugin.LOCALIZED_KEY_BONUS);
			event.getPlayer()
					.sendMessage(ChatColor.DARK_GREEN + bonus + "[x" + rewardCount + "] " + block.getType().toString());
			reward(block, rewardCount);
		} else {
			plugin.debugInfo("No luck on Earthday");
			event.getPlayer().sendMessage(ChatColor.RED + ":(");
		}

	}

	private void reward(Block block, int rewardCount) {

		Material reward = Material.WHEAT;
		if (block.getType() == Material.CROPS) {
			reward = Material.WHEAT;
		} else if (block.getType() == Material.CARROT) {
			reward = Material.CARROT_ITEM;
		} else if (block.getType() == Material.POTATO) {
			reward = Material.POTATO_ITEM;
		} else {
			reward = Material.GRASS;
		}

		dropItemsInStacks(block.getLocation(), reward, rewardCount,
				plugin.getConfig().getString(YearmarkedPlugin.CONFIG_KEY_EARTHDAY) + " " + reward.name());
	}
}
