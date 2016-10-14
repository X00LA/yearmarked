package com.blocktyper.yearmarked.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.blocktyper.yearmarked.DayOfWeekEnum;
import com.blocktyper.yearmarked.YearmarkedCalendar;
import com.blocktyper.yearmarked.YearmarkedPlugin;

public class FishfrydayListener extends AbstractListener {

	public FishfrydayListener(YearmarkedPlugin plugin) {
		super(plugin);

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCatchFish(PlayerFishEvent event) {
		YearmarkedCalendar cal = new YearmarkedCalendar(event.getPlayer().getWorld());
		if (!cal.getDayOfWeekEnum().equals(DayOfWeekEnum.FISHFRYDAY)) {
			return;
		}
		
		if (!worldEnabled(event.getPlayer().getWorld().getName(), plugin.getConfig().getString(DayOfWeekEnum.FISHFRYDAY.getDisplayKey()))) {
			return;
		}

		if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
			String doubleXp = plugin.getLocalizedMessage(YearmarkedPlugin.LOCALIZED_KEY_DOUBLE_XP);
			event.getPlayer().sendMessage(ChatColor.DARK_GREEN + doubleXp);
			event.setExpToDrop(event.getExpToDrop() * 2);

			boolean isOpLucky = event.getPlayer().isOp()
					&& plugin.getConfig().getBoolean(YearmarkedPlugin.CONFIG_KEY_FISHFRYDAY_OP_LUCK, true);

			int percentChanceOfDiamond = plugin.getConfig()
					.getInt(YearmarkedPlugin.CONFIG_KEY_FISHFRYDAY_PERCENT_CHANCE_DIAMOND, 1);
			int percentChanceOfEmerald = plugin.getConfig()
					.getInt(YearmarkedPlugin.CONFIG_KEY_FISHFRYDAY_PERCENT_CHANCE_EMERALD, 10);
			int percentChanceOfGrass = plugin.getConfig()
					.getInt(YearmarkedPlugin.CONFIG_KEY_FISHFRYDAY_PERCENT_CHANCE_GRASS, 10);
			int percentChanceOfThordfish = plugin.getConfig()
					.getInt(YearmarkedPlugin.CONFIG_KEY_FISHFRYDAY_PERCENT_CHANCE_THORDFISH, 10);

			if (isOpLucky || plugin.rollIsLucky(percentChanceOfDiamond)) {
				String message = plugin.getLocalizedMessage(YearmarkedPlugin.LOCALIZED_KEY_FISH_HAD_DIAMOND);
				doReward(event.getPlayer(), Material.DIAMOND, message, ChatColor.BLUE,
						plugin.getConfig().getString(YearmarkedPlugin.CONFIG_KEY_FISHFRYDAY) + " "
								+ Material.DIAMOND.name());
			}

			if (isOpLucky || plugin.rollIsLucky(percentChanceOfEmerald)) {
				String message = plugin.getLocalizedMessage(YearmarkedPlugin.LOCALIZED_KEY_FISH_HAD_EMERALD);
				doReward(event.getPlayer(), Material.EMERALD, message, ChatColor.GREEN,
						plugin.getConfig().getString(YearmarkedPlugin.CONFIG_KEY_FISHFRYDAY) + " "
								+ Material.EMERALD.name());
			}

			if (isOpLucky || plugin.rollIsLucky(percentChanceOfGrass)) {
				doReward(event.getPlayer(), Material.GRASS, null, ChatColor.GREEN,
						plugin.getConfig().getString(YearmarkedPlugin.CONFIG_KEY_FISHFRYDAY) + " "
								+ Material.GRASS.name());
			}

			if (isOpLucky || plugin.rollIsLucky(percentChanceOfThordfish)) {
				if (plugin.getNameOfThordfish() != null && !plugin.getNameOfThordfish().isEmpty()) {
					doReward(event.getPlayer(), Material.RAW_FISH, plugin.getNameOfThordfish() + "!",
							ChatColor.DARK_GREEN, plugin.getNameOfThordfish());
				}
			}

			if (isOpLucky) {
				event.getPlayer().sendMessage(ChatColor.GOLD + "OP!");
			}

		}
	}


	private void doReward(Player player, Material reward, String message, ChatColor color, String customName) {
		if (reward != null) {
			ItemStack item = new ItemStack(reward);

			if (customName != null) {
				ItemMeta itemMeta = item.getItemMeta();
				if (itemMeta != null) {
					itemMeta.setDisplayName(customName);
					item.setItemMeta(itemMeta);
				} else {
					plugin.debugWarning("Could not set custom name for bonus fishing loot: " + customName);
				}
			}
			player.getWorld().dropItem(player.getLocation(), item);
			if (message != null)
				player.sendMessage(color + message);
		}
	}
}
