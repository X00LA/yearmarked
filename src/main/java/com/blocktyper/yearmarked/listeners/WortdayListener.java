package com.blocktyper.yearmarked.listeners;

import java.util.Random;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;

import com.blocktyper.yearmarked.MinecraftCalendar;
import com.blocktyper.yearmarked.MinecraftDayOfWeekEnum;
import com.blocktyper.yearmarked.YearmarkedPlugin;

public class WortdayListener extends AbstractListener {

	private Random random = new Random();

	public WortdayListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onCropsBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();

		if (block.getType() != Material.CROPS && block.getType() != Material.CARROT
				&& block.getType() != Material.POTATO) {
			return;
		}

		MinecraftCalendar cal = new MinecraftCalendar(block.getWorld());
		if (!cal.getDayOfWeekEnum().equals(MinecraftDayOfWeekEnum.WORTTAG)) {
			return;
		}

		if (block.getType() != Material.NETHER_WARTS) {
			return;
		}

		if (((Crops) block.getState().getData()).getState() != CropState.RIPE) {
			return;
		}

		event.getPlayer().sendMessage("reward");
		int rewardCount = random.nextInt(2) + 1;
		event.getPlayer().sendMessage("reward[x" + rewardCount + "] " + block.getType().toString());
		block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.NETHER_WARTS, rewardCount));
	}
}
