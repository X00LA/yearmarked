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
import com.blocktyper.yearmarked.DayOfWeekEnum;
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
		if (!cal.getDayOfWeekEnum().equals(DayOfWeekEnum.DIAMONDAY)) {
			return;
		}
		
		if(!plugin.getConfig().getBoolean(YearmarkedPlugin.CONFIG_KEY_DIAMONDAY_BONUS_DIAMONDS, true)){
			plugin.debugInfo(YearmarkedPlugin.CONFIG_KEY_DIAMONDAY_BONUS_DIAMONDS + ": false");
			return;
		}
		
		if (!worldEnabled(block.getWorld().getName(), plugin.getConfig().getString(DayOfWeekEnum.DIAMONDAY.getDisplayKey()))) {
			return;
		}

		ItemStack inhand = event.getPlayer().getEquipment().getItemInMainHand();
		final Enchantment SILK_TOUCH = new EnchantmentWrapper(33);
		if (inhand.containsEnchantment(SILK_TOUCH)) {
			return;
		}
		
		int high = plugin.getConfig().getInt(YearmarkedPlugin.CONFIG_KEY_DIAMONDAY_BONUS_DIAMONDS_RANGE_HIGH, 3);
		int low = plugin.getConfig().getInt(YearmarkedPlugin.CONFIG_KEY_DIAMONDAY_BONUS_DIAMONDS_RANGE_LOW, 1);

		int rewardCount = random.nextInt(high + 1);
		
		if(rewardCount < low){
			rewardCount = low;
		}
		
		String bonus = plugin.getLocalizedMessage(YearmarkedPlugin.LOCALIZED_KEY_BONUS);
		
		if(rewardCount > 0){
			event.getPlayer().sendMessage(ChatColor.BLUE + bonus + "[x" + rewardCount + "] " + block.getType().toString());
			dropItemsInStacks(block.getLocation(), Material.DIAMOND, rewardCount, plugin.getConfig().getString(YearmarkedPlugin.CONFIG_KEY_DIAMONDAY) + " " + Material.DIAMOND.name());
		}else{
			plugin.debugInfo("No luck on Diamonday");
			event.getPlayer().sendMessage(ChatColor.RED + ":(");
		}
		
	}
}
