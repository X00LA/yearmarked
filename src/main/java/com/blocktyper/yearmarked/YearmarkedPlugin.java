package com.blocktyper.yearmarked;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.blocktyper.plugin.BlockTyperPlugin;
import com.blocktyper.yearmarked.listeners.DiamondayListener;
import com.blocktyper.yearmarked.listeners.EarthdayListener;
import com.blocktyper.yearmarked.listeners.FeathersdayListener;
import com.blocktyper.yearmarked.listeners.FishfrydayListener;
import com.blocktyper.yearmarked.listeners.MonsoondayListener;
import com.blocktyper.yearmarked.listeners.ThordfishListener;
import com.blocktyper.yearmarked.listeners.WortagListener;

public class YearmarkedPlugin extends BlockTyperPlugin implements Listener {

	
	public static final String RESOURCE_NAME = "com.blocktyper.yearmarked.resources.YearmarkedMessages";
	
	
	public static String DEFAULT_WORLD = "world";

	public static String KEY_WORLDS = "yearmarked-worlds";

	public static String LOCALIZED_KEY_WORLD = "yearmarked.world";
	public static String LOCALIZED_KEY_BONUS = "yearmarked.bonus";
	public static String LOCALIZED_KEY_FALL_DAMAGE_PREVENTED = "yearmarked.fall.damage.prevented";
	public static String LOCALIZED_KEY_DOUBLE_XP = "yearmarked.double.xp";
	public static String LOCALIZED_KEY_FISH_HAD_DIAMOND = "yearmarked.fish.had.diamond";
	public static String LOCALIZED_KEY_FISH_HAD_EMERALD = "yearmarked.fish.had.emerald";
	public static String LOCALIZED_KEY_TODAY_IS = "yearmarked.today.is";

	public static String LOCALIZED_KEY_IT_IS_DAY_NUMBER = "yearmarked.it.is.day.number";
	public static String LOCALIZED_KEY_OF_MONTH_NUMBER = "yearmarked.of.month.number";
	public static String LOCALIZED_KEY_OF_YEAR_NUMBER = "yearmarked.of.year.number";

	int checkTimeInterval = 5;// sec

	List<String> worlds;
	
	Set<String> playersExemptFromLightning = null;
	
	
	
	
	
	// begin localization
	private ResourceBundle bundle = null;

	public ResourceBundle getBundle() {
		if (bundle == null)
			bundle = ResourceBundle.getBundle(RESOURCE_NAME, locale);
		return bundle;
	}
	// end localization
		
		

	public void onEnable() {
		super.onEnable();
		createConfig();

		getServer().getPluginManager().registerEvents(this, this);

	
		info("loaded worlds");
		worlds = getConfig().getStringList(KEY_WORLDS);
		if (worlds != null) {
			if (worlds.isEmpty()) {
				info("[empty]");
			} else {
				for (String world : worlds) {
					info("   -" + world);
				}
			}

		} else {
			info("[null]");
		}

		if (worlds == null || worlds.isEmpty()) {
			info("adding default world: " + DEFAULT_WORLD);
			worlds.add("world");
		}

		info("starting world monitors");

		startWorldMonitors();
		registerListeners();

	}

	private void startWorldMonitors() {

		for (String world : worlds) {
			try {
				startWorldMonitor(world);
			} catch (IllegalArgumentException e) {
				warning("IllegalArgumentException while starting world moniror[" + world + "]. Message: "
						+ e.getMessage());
				// e.printStackTrace();
				continue;
			} catch (IllegalStateException e) {
				warning("IllegalArgumentException while starting world moniror[" + world + "]. Message: "
						+ e.getMessage());
				// e.printStackTrace();
				continue;
			} catch (Exception e) {
				warning(
						"General Exception while starting world moniror[" + world + "]. Message: " + e.getMessage());
				// e.printStackTrace();
				continue;
			}
		}
	}

	private void startWorldMonitor(String world) {
		info("Loading World" + ": " + world);
		TimeMonitor timeMonitor = new TimeMonitor(this, world);

		if (timeMonitor.getWorld() == null) {
			warning( "   -" + world + " was no recognized");
			return;
		}else{
			info( "   -" + world + " was loaded");
		}

		MinecraftCalendar cal = new MinecraftCalendar(timeMonitor.getWorld());

		try {
			timeMonitor.sendDayInfo(cal, timeMonitor.getWorld().getPlayers());
		} catch (Exception e) {
			this.warning("Errors while sending day info. Message: " + e.getMessage());
			return;
			// e.printStackTrace();
		}

		timeMonitor.checkForDayChange(cal);

		this.info("Checking time every " + checkTimeInterval + " sec.");
		timeMonitor.runTaskTimer(this, checkTimeInterval, checkTimeInterval * 20L);
	}

	private void registerListeners() {
		new MonsoondayListener(this);
		new EarthdayListener(this);
		new WortagListener(this);
		new ThordfishListener(this);// Donnerstag is handled by logic in TimeMonitor.  ThorsdayListener is only for paying attention to when a user hits a tree with a Thordfish
		new FishfrydayListener(this);
		new DiamondayListener(this);
		new FeathersdayListener(this);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void playerJoin(PlayerJoinEvent event) {
		initPlayer(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void playerChangedWorld(PlayerChangedWorldEvent event) {
		initPlayer(event.getPlayer());
	}

	private void initPlayer(Player player) {
		MinecraftCalendar cal = new MinecraftCalendar(player.getWorld().getFullTime());
		sendPlayerDayInfo(player, cal);
	}

	private void sendPlayerDayInfo(Player player, MinecraftCalendar cal) {
		info("sending day info to " + player.getName());
		if (worlds.contains(player.getWorld().getName())) {
			info("really sending day info to " + player.getName());
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



	public Set<String> getPlayersExemptFromLightning() {
		return playersExemptFromLightning;
	}



	public void setPlayersExemptFromLightning(Set<String> playersExemptFromLightning) {
		this.playersExemptFromLightning = playersExemptFromLightning;
	}
	
	
}
