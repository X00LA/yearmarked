package com.blocktyper.yearmarked.listeners;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;

import com.blocktyper.yearmarked.MinecraftCalendar;
import com.blocktyper.yearmarked.MinecraftDayOfWeekEnum;
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
		if (!cal.getDayOfWeekEnum().equals(MinecraftDayOfWeekEnum.WORTAG)) {
			return;
		}

		if (block.getType() != Material.NETHER_WARTS) {
			return;
		}

		if (((Crops) block.getState().getData()).getState() != CropState.RIPE) {
			return;
		}

		event.getPlayer().sendMessage("reward");
		int rewardCount = random.nextInt(3) + 1;
		event.getPlayer().sendMessage(ChatColor.DARK_PURPLE + "reward[x" + rewardCount + "] " + block.getType().toString());
		block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.NETHER_WARTS, rewardCount));
	}
}
