package com.blocktyper.yearmarked.listeners;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.blocktyper.yearmarked.MinecraftCalendar;
import com.blocktyper.yearmarked.MinecraftDayOfWeekEnum;
import com.blocktyper.yearmarked.YearmarkedPlugin;

public class FishfrydayListener extends AbstractListener {

	private Random random = new Random();

	public static final String THORDFISH = "Thordfish";

	public FishfrydayListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCatchFish(PlayerFishEvent event) {
		MinecraftCalendar cal = new MinecraftCalendar(event.getPlayer().getWorld());
		if (!cal.getDayOfWeekEnum().equals(MinecraftDayOfWeekEnum.FISHFRYDAY)) {
			return;
		}

		if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
			String doubleXp = plugin.getLocalizedMessage(YearmarkedPlugin.LOCALIZED_KEY_DOUBLE_XP);
			event.getPlayer().sendMessage(ChatColor.DARK_GREEN + doubleXp);
			event.setExpToDrop(event.getExpToDrop() * 2);

			Material reward = null;
			String message = null;
			ChatColor color = null;

			if (event.getPlayer().isOp() && plugin.getConfig().getBoolean("yearmarked.op.luck", true)) {
				for (int i = 0; i < 3; i++) {
					if (i == 0) {
						reward = Material.DIAMOND;
						message = plugin.getLocalizedMessage(YearmarkedPlugin.LOCALIZED_KEY_FISH_HAD_DIAMOND);
						color = ChatColor.BLUE;
					} else if (i == 1) {
						reward = Material.EMERALD;
						message = plugin.getLocalizedMessage(YearmarkedPlugin.LOCALIZED_KEY_FISH_HAD_EMERALD);
						color = ChatColor.GREEN;
					} else if (i == 2) {
						reward = Material.GRASS;
						message = "OP LUCK";
						color = ChatColor.RED;
					}

					doReward(event.getPlayer(), reward, message, color);
				}

			} else {
				if (random.nextInt(100) == 50) {
					reward = Material.DIAMOND;
					message = plugin.getLocalizedMessage(YearmarkedPlugin.LOCALIZED_KEY_FISH_HAD_DIAMOND);
					color = ChatColor.BLUE;
				} else if (random.nextInt(10) == 5) {
					reward = Material.EMERALD;
					message = plugin.getLocalizedMessage(YearmarkedPlugin.LOCALIZED_KEY_FISH_HAD_EMERALD);
					color = ChatColor.GREEN;
				} else if (random.nextInt(10) == 5) {
					reward = Material.GRASS;
					message = null;
					color = null;
				}
			}

			if (reward != null) {
				ItemStack thordfish = new ItemStack(Material.RAW_FISH);
				ItemMeta meta = thordfish.getItemMeta();
				meta.setDisplayName(THORDFISH);
				thordfish.setItemMeta(meta);
				event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), thordfish);
				event.getPlayer().sendMessage(ChatColor.DARK_GREEN + THORDFISH + "!");

				doReward(event.getPlayer(), reward, message, color);
			}

		}
	}

	private void doReward(Player player, Material reward, String message, ChatColor color) {
		if (reward != null) {
			ItemStack emeraldOrDiamond = new ItemStack(reward);
			player.getWorld().dropItem(player.getLocation(), emeraldOrDiamond);
			if (message != null)
				player.sendMessage(color + message);
		}
	}
}
