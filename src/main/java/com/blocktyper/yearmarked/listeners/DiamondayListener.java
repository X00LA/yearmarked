package com.blocktyper.yearmarked.listeners;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.yearmarked.MinecraftCalendar;
import com.blocktyper.yearmarked.MinecraftDayOfWeekEnum;
import com.blocktyper.yearmarked.YearmarkedPlugin;

import net.md_5.bungee.api.ChatColor;

public class DiamondayListener extends AbstractListener {

	private Random random = new Random();

	public DiamondayListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onDiamondBlockBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();

		if (!block.getType().equals(Material.DIAMOND_ORE)) {
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

		int rewardCount = random.nextInt(2) + 1;
		String bonus = plugin.getLocalizedMessage(YearmarkedPlugin.LOCALIZED_KEY_FALL_DAMAGE_PREVENTED);
		event.getPlayer().sendMessage(ChatColor.BLUE + bonus + "[x" + rewardCount + "] " + block.getType().toString());
		block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.DIAMOND, rewardCount));
	}
}
