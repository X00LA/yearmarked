package com.blocktyper.yearmarked;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.ResourceBundle;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.WeatherType;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.plugin.java.JavaPlugin;

import com.blocktyper.localehelper.LocaleHelper;

public class YearmarkedPlugin extends JavaPlugin implements Listener {

	private Random random = new Random();

	public static String KEY_WORLDS = "yearmarked-worlds";
	private String LOCALIZED_KEY_WORLD = "yearmarked.world";

	int checkTimeInterval = 5;// sec

	List<String> worlds;

	public void onEnable() {
		createConfig();

		getServer().getPluginManager().registerEvents(this, this);

		saveDefaultConfig();
		resourceName = "com.blocktyper.yearmarked.resources.YearmarkedMessages";
		locale = new LocaleHelper(getLogger(), getFile() != null ? getFile().getParentFile() : null).getLocale();

		worlds = getConfig().getStringList(KEY_WORLDS);

		if (worlds == null || worlds.isEmpty()) {
			worlds.add("world");
		}

		for (String world : worlds) {
			getLogger().info(getLocalizedMessage(LOCALIZED_KEY_WORLD) + ": " + world);
			TimeMonitor timeMonitor = new TimeMonitor(this, world);

			MinecraftCalendar cal = new MinecraftCalendar(timeMonitor.getWorld());

			timeMonitor.sendDayInfo(cal, timeMonitor.getWorld().getPlayers());

			timeMonitor.checkForDayChange(cal);

			this.getLogger().info("Checking time every " + checkTimeInterval + "sec.");
			timeMonitor.runTaskTimer(this, checkTimeInterval, checkTimeInterval * 20L);
		}

	}

	@EventHandler
	public void onCropsBreak(BlockBreakEvent event) {
		event.getPlayer().sendMessage("block broken");
		final Block block = event.getBlock();
		
		
		/*
		 * case CARROT:
        case POTATO:
            return blockState.getRawData() == CropState.RIPE.getData();

        case CROPS:
            return ((Crops) blockState.getData()).getState() == CropState.RIPE;
		 */
		
		if (block.getType() == Material.CROPS && ((Crops) block.getState().getData()).getState() == CropState.RIPE) {
			event.getPlayer().sendMessage("crops");
			MinecraftCalendar cal = new MinecraftCalendar(block.getWorld());
			if (cal.getDayOfWeekEnum().equals(MinecraftDayOfWeekEnum.EARTHDAY)) {
				
				event.getPlayer().sendMessage("reward");
				int rewardCount = random.nextInt(3) + 1;
				event.getPlayer().sendMessage("count: " + rewardCount);
				rewardWheat(block, rewardCount);
			}else{
				event.getPlayer().sendMessage("blockState:" + block.getState());
				event.getPlayer().sendMessage(cal.getDayOfWeek() + "-" + cal.getDayOfWeekEnum().getDisplayName());
			}
		}else{
			event.getPlayer().sendMessage("blockState:" + (block.getState() != null ? block.getState().toString() : "null"));
		}
		event.getPlayer().sendMessage("end.");
	}

	private void rewardWheat(Block block, int rewardCount) {

		block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.WHEAT, rewardCount));
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void playerJoin(PlayerJoinEvent event) {
		initPlayer(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void playerChangedWorld(PlayerChangedWorldEvent event) {
		initPlayer(event.getPlayer());
	}
	
	private void initPlayer(Player player){
		MinecraftCalendar cal = new MinecraftCalendar(player.getWorld().getFullTime());
		sendPlayerDayInfo(player, cal);
		if(MinecraftDayOfWeekEnum.MONSOONDAY.equals(cal.getDayOfWeekEnum())){
			player.setPlayerWeather(WeatherType.DOWNFALL);
		}else{
			player.setPlayerWeather(WeatherType.CLEAR);
		}
	}

	private void sendPlayerDayInfo(Player player, MinecraftCalendar cal) {
		getLogger().info("sending day info to " + player.getName());
		if (worlds.contains(player.getWorld().getName())) {
			getLogger().info("really sending day info to " + player.getName());
			List<Player> playerInAList = new ArrayList<Player>();
			playerInAList.add(player);
			TimeMonitor timeMonitor = new TimeMonitor(this, player.getWorld().getName());
			timeMonitor.sendDayInfo(cal, playerInAList);
		}
	}

	// begin config file initialization
	private void createConfig() {
		if (!new File(getDataFolder(), "config.yml").exists()) {
			saveDefaultConfig();
		}

	}
	// end config file initialization

	private Locale locale = null;
	private ResourceBundle bundle = null;
	private boolean bundleLoadFailed = false;

	private String resourceName;

	public String getLocalizedMessage(String key) {

		String value = key;
		try {
			if (bundle == null) {

				if (locale == null) {
					getLogger().info("Using default locale.");
					locale = Locale.getDefault();
				}

				try {
					bundle = ResourceBundle.getBundle(resourceName, locale);
				} catch (Exception e) {
					getLogger().warning(resourceName + " bundle did not load successfully.");
				}

				if (bundle == null) {
					getLogger().warning(
							"Messages will appear as dot separated key names.  Please remove this plugin from your plugin folder if this behaviour is not desired.");
					bundleLoadFailed = true;
					return key;
				} else {
					getLogger().info(resourceName + " bundle loaded successfully.");
				}
			}

			if (bundleLoadFailed) {
				return key;
			}

			value = bundle.getString(key);

			value = key != null ? (value != null && !value.trim().isEmpty() ? value : key) : "null key";
		} catch (Exception e) {
			getLogger().warning(
					"Unexpected error getting localized string for key(" + key + "). Message: " + e.getMessage());
		}
		return value;
	}

	// end localization

}
