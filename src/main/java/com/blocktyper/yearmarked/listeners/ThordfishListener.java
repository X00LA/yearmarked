package com.blocktyper.yearmarked.listeners;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.WeatherType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.yearmarked.MinecraftCalendar;
import com.blocktyper.yearmarked.MinecraftDayOfWeekEnum;
import com.blocktyper.yearmarked.YearmarkedPlugin;

public class ThordfishListener extends AbstractListener {

	public ThordfishListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void blockDamage(BlockDamageEvent event) {
		
		MinecraftCalendar cal = new MinecraftCalendar(event.getPlayer().getWorld());
		MinecraftDayOfWeekEnum dayOfWeekEnum = cal.getDayOfWeekEnum();
		if (!dayOfWeekEnum.equals(MinecraftDayOfWeekEnum.MONSOONDAY) && !dayOfWeekEnum.equals(MinecraftDayOfWeekEnum.DONNERSTAG)) {
			plugin.debugInfo("Not " + MinecraftDayOfWeekEnum.MONSOONDAY + " or " + MinecraftDayOfWeekEnum.DONNERSTAG);
			return;
		}
		plugin.debugInfo("BlockDamageEvent - Material " + event.getBlock().getType().name());

		ItemStack itemInHand = event.getItemInHand();

		if (itemInHand == null) {
			plugin.debugWarning("Not holding an item");
			return;
		}

		if (!itemInHand.getType().equals(Material.RAW_FISH)) {
			plugin.debugWarning("Not holding a fish");
			return;
		}

		if (itemInHand.getItemMeta() == null || itemInHand.getItemMeta().getDisplayName() == null) {
			plugin.debugWarning("Not holding fish with a name.");
			return;
		}

		String itemName = itemInHand.getItemMeta().getDisplayName();

		boolean isThordfish = itemName.equals(FishfrydayListener.THORDFISH);

		if (!isThordfish) {
			plugin.debugInfo("Not a " + FishfrydayListener.THORDFISH);
			return;
		}
		
		if(dayOfWeekEnum.equals(MinecraftDayOfWeekEnum.MONSOONDAY)){
			if(event.getPlayer().getPlayerWeather().equals(WeatherType.CLEAR)){
				event.getPlayer().sendMessage(ChatColor.DARK_BLUE + ":(");
				event.getPlayer().setPlayerWeather(WeatherType.DOWNFALL);
			}else{
				event.getPlayer().sendMessage(ChatColor.AQUA + ":)");
				event.getPlayer().setPlayerWeather(WeatherType.CLEAR);
			}
			
		}else if(dayOfWeekEnum.equals(MinecraftDayOfWeekEnum.DONNERSTAG)){
			Set<String> playerExemptFromLightning = plugin.getPlayersExemptFromLightning();
			if(playerExemptFromLightning == null){
				playerExemptFromLightning = new HashSet<String>();
			}
			if(playerExemptFromLightning.contains(event.getPlayer().getName())){
				playerExemptFromLightning.remove(event.getPlayer().getName());
				event.getPlayer().sendMessage(ChatColor.RED + ":(");
			}else{
				event.getPlayer().sendMessage(ChatColor.GREEN + ":)");
				event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, 1);
				playerExemptFromLightning.add(event.getPlayer().getName());
				
				if(itemInHand.getAmount() > 1){
					itemInHand.setAmount(itemInHand.getAmount() - 1);
				}else{
					event.getPlayer().getInventory().remove(itemInHand);
				}
			}
			plugin.setPlayersExemptFromLightning(playerExemptFromLightning);
		}
	}

}
