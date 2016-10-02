package com.blocktyper.yearmarked.listeners;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.yearmarked.MinecraftCalendar;
import com.blocktyper.yearmarked.MinecraftDayOfWeekEnum;
import com.blocktyper.yearmarked.YearmarkedPlugin;

public class DiamondayListener extends AbstractListener {

	private Random random = new Random();

	public DiamondayListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onDiamondBlockBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();

		if (block.getType() != Material.DIAMOND) {
			return;
		}

		MinecraftCalendar cal = new MinecraftCalendar(block.getWorld());
		if (!cal.getDayOfWeekEnum().equals(MinecraftDayOfWeekEnum.DIAMONDAY)) {
			return;
		}

		ItemStack inhand = event.getPlayer().getEquipment().getItemInMainHand();
		final Enchantment SILK_TOUCH = new EnchantmentWrapper(33);
		if (inhand.containsEnchantment(SILK_TOUCH)) {
			return;
		}

		event.getPlayer().sendMessage("reward");
		int rewardCount = random.nextInt(1) + 1;
		event.getPlayer().sendMessage("reward[x" + rewardCount + "] " + block.getType().toString());
		reward(block, rewardCount);
	}

	private void reward(Block block, int rewardCount) {
		block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.DIAMOND, rewardCount));
	}
}
