package com.blocktyper.yearmarked;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.blocktyper.plugin.BlockTyperPlugin;
import com.blocktyper.yearmarked.commands.YmCommand;
import com.blocktyper.yearmarked.listeners.DiamondayListener;
import com.blocktyper.yearmarked.listeners.EarthdayListener;
import com.blocktyper.yearmarked.listeners.FeathersdayListener;
import com.blocktyper.yearmarked.listeners.FishfrydayListener;
import com.blocktyper.yearmarked.listeners.MonsoondayListener;
import com.blocktyper.yearmarked.listeners.SuperCreeperDamageListener;
import com.blocktyper.yearmarked.listeners.ThordfishListener;
import com.blocktyper.yearmarked.listeners.WortagListener;

import net.md_5.bungee.api.ChatColor;

public class YearmarkedPlugin extends BlockTyperPlugin implements Listener {

	private Random random = new Random();

	public static final String RESOURCE_NAME = "com.blocktyper.yearmarked.resources.YearmarkedMessages";

	public static String DEFAULT_WORLD = "world";
	

	int checkTimeInterval = 5;// sec

	private List<String> worlds;

	private Set<String> playersExemptFromLightning = null;

	private String nameOfThordfish = null;
	private String nameOfFishSword = null;
	private String nameOfFishArrow = null;

	
	public void onEnable() {
		super.onEnable();
		createConfig();
		
		info("MONSOONDAY=" + getConfig().getString(ConfigKeyEnum.MONSOONDAY.getKey()));
		info("EARTHDAY=" + getConfig().getString(ConfigKeyEnum.EARTHDAY.getKey()));
		info("WORTAG=" + getConfig().getString(ConfigKeyEnum.WORTAG.getKey()));
		info("DONNERSTAG=" + getConfig().getString(ConfigKeyEnum.DONNERSTAG.getKey()));
		info("FISHFRYDAY=" + getConfig().getString(ConfigKeyEnum.FISHFRYDAY.getKey()));
		info("DIAMONDAY=" + getConfig().getString(ConfigKeyEnum.DIAMONDAY.getKey()));
		info("FEATHERSDAY=" + getConfig().getString(ConfigKeyEnum.FEATHERSDAY.getKey()));

		getServer().getPluginManager().registerEvents(this, this);

		info(getLocalizedMessage(LocalizedMessageEnum.WORLDS.getKey()) + ": ");
		worlds = getConfig().getStringList(ConfigKeyEnum.WORLDS.getKey());
		if (worlds != null) {
			if (worlds.isEmpty()) {
				info("[empty]");
			} else {
				for (String world : worlds) {
					info("  - " + world);
				}
			}

		} else {
			info("[null]");
		}

		if (worlds == null || worlds.isEmpty()) {
			worlds = worlds != null ? worlds : new ArrayList<String>();
			info("adding default world: " + DEFAULT_WORLD);
			worlds.add("world");
		}
		
		nameOfThordfish = getConfig().getString(ConfigKeyEnum.RECIPE_THORDFISH.getKey());
		nameOfFishSword = getConfig().getString(ConfigKeyEnum.RECIPE_FISH_SWORD.getKey());
		nameOfFishArrow = getConfig().getString(ConfigKeyEnum.RECIPE_FISH_ARROW.getKey());

		info("starting world monitors");
		
		startWorldMonitors();
		registerListeners();
		registerCommands();

	}

	private void startWorldMonitors() {

		for (String world : worlds) {
			try {
				startWorldMonitor(world);
			} catch (IllegalArgumentException e) {
				warning("IllegalArgumentException while starting world monitor[" + world + "]. Message: "
						+ e.getMessage());
				// e.printStackTrace();
				continue;
			} catch (IllegalStateException e) {
				warning("IllegalArgumentException while starting world monitor[" + world + "]. Message: "
						+ e.getMessage());
				// e.printStackTrace();
				continue;
			} catch (Exception e) {
				warning("General Exception while starting world monitor[" + world + "]. Message: " + e.getMessage());
				// e.printStackTrace();
				continue;
			}
		}
	}

	private void startWorldMonitor(String world) {
		info("LOADING... " + getLocalizedMessage(LocalizedMessageEnum.WORLD.getKey()) + "(" + world + ")");
		TimeMonitor timeMonitor = new TimeMonitor(this, world);

		if (timeMonitor.getWorld() == null) {
			warning("   -" + world + " was not recognized");
			return;
		} else {
			info("   -" + world + " was loaded");
		}

		YearmarkedCalendar cal = new YearmarkedCalendar(timeMonitor.getWorld());

		try {
			sendDayInfo(cal, timeMonitor.getWorld().getPlayers());
		} catch (Exception e) {
			this.warning("Errors while sending day info. Message: " + e.getMessage());
			return;
			// e.printStackTrace();
		}

		timeMonitor.checkForDayChange(cal);

		this.info("Checking time every " + checkTimeInterval + " sec.");
		timeMonitor.runTaskTimer(this, checkTimeInterval, checkTimeInterval * 20L);
	}

	private void registerCommands() {
		YmCommand yearmarkedCommand = new YmCommand(this);
		this.getCommand("yearmarked").setExecutor(yearmarkedCommand);
		this.getCommand("ym").setExecutor(yearmarkedCommand);
		getLogger().info("'/yearmarked' registered to YmCommand");
		getLogger().info("'/ym' registered to YmCommand");
	}

	private void registerListeners() {
		new MonsoondayListener(this);
		new EarthdayListener(this);
		new WortagListener(this);
		new ThordfishListener(this);// Donnerstag is handled by logic in
									// TimeMonitor. ThorsdayListener is only for
									// paying attention to when a user hits a
									// tree with a Thordfish. It pertains to both Moonsoonday and DOnnerstag
		new SuperCreeperDamageListener(this);//this only pertains to Donnerstag
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
		YearmarkedCalendar cal = new YearmarkedCalendar(player.getWorld().getFullTime());
		sendPlayerDayInfo(player, cal);
	}

	private void sendPlayerDayInfo(Player player, YearmarkedCalendar cal) {
		info("sending day info to " + player.getName());
		if (worlds.contains(player.getWorld().getName())) {
			info("really sending day info to " + player.getName());
			List<Player> playerInAList = new ArrayList<Player>();
			playerInAList.add(player);
			sendDayInfo(cal, playerInAList);
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

	public String getNameOfThordfish() {
		return nameOfThordfish;
	}

	public String getNameOfFishSword() {
		return nameOfFishSword;
	}

	public String getNameOfFishArrow() {
		return nameOfFishArrow;
	}
	
	
	//public helpers
	public boolean rollIsLucky(double percentChanceOfTrue) {
		if (percentChanceOfTrue <= 0 || (percentChanceOfTrue < 100 && random.nextDouble() > percentChanceOfTrue)) {
			return false;
		}else if(percentChanceOfTrue >= 100){
			return true;
		}else if(percentChanceOfTrue <= random.nextDouble()){
			return true;
		}
		return false;
	}
	
	public boolean worldEnabled(String world){
		return worlds != null && worlds.contains(world);
	}
	
	public void sendDayInfo(YearmarkedCalendar cal, List<Player> players) {

		plugin.debugInfo("sendDayInfo --> displayKey: " + cal.getDayOfWeekEnum().getDisplayKey());
		String dayName = plugin.getConfig().getString(cal.getDayOfWeekEnum().getDisplayKey(), "A DAY");
		plugin.debugInfo("sendDayInfo --> dayName: " + dayName);
		String todayIs = String.format(plugin.getLocalizedMessage(LocalizedMessageEnum.TODAY_IS.getKey()), dayName);
		String dayOfMonthMessage = new MessageFormat(
				plugin.getLocalizedMessage(LocalizedMessageEnum.IT_IS_DAY_NUMBER.getKey())).format(
						new Object[] { cal.getDayOfMonth() + "", cal.getMonthOfYear() + "", cal.getYear() + "" });

		if (players != null && !players.isEmpty()) {
			for (Player player : players) {
				player.sendMessage(ChatColor.GREEN + "#----------------");
				player.sendMessage(ChatColor.GREEN + "#----------------");
				player.sendMessage(ChatColor.YELLOW + todayIs);
				player.sendMessage(dayOfMonthMessage);
				player.sendMessage(ChatColor.GREEN + "#----------------");
				player.sendMessage(ChatColor.GREEN + "#----------------");
			}
		}
	}
	
	
	
	
	
	
	private ResourceBundle bundle = null;

	public ResourceBundle getBundle() {
		if (bundle == null)
			bundle = ResourceBundle.getBundle(RESOURCE_NAME, locale);
		return bundle;
	}
	///////////////////////////
	// end localization ///////
	///////////////////////////


}
