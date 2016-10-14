package com.blocktyper.yearmarked;

import java.io.File;
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
import com.blocktyper.yearmarked.commands.ChangeTimeCommand;
import com.blocktyper.yearmarked.listeners.DiamondayListener;
import com.blocktyper.yearmarked.listeners.EarthdayListener;
import com.blocktyper.yearmarked.listeners.FeathersdayListener;
import com.blocktyper.yearmarked.listeners.FishfrydayListener;
import com.blocktyper.yearmarked.listeners.MonsoondayListener;
import com.blocktyper.yearmarked.listeners.SuperCreeperDamageListener;
import com.blocktyper.yearmarked.listeners.ThordfishListener;
import com.blocktyper.yearmarked.listeners.WortagListener;

public class YearmarkedPlugin extends BlockTyperPlugin implements Listener {

	private Random random = new Random();

	public static final String RESOURCE_NAME = "com.blocktyper.yearmarked.resources.YearmarkedMessages";

	public static String DEFAULT_WORLD = "world";

	public static String CONFIG_KEY_WORLDS = "yearmarked-worlds";
	

	// MONSOONDAY
	public static final String CONFIG_KEY_MONSOONDAY = "yearmarked-monsoonday";
	public static final String CONFIG_KEY_MONSOONDAY_RAIN = "yearmarked-monsoonday-rain";
	public static final String CONFIG_KEY_MONSOONDAY_ALLOW_RAIN_TOGGLE_WITH_THORDFISH = "yearmarked-monsoonday-allow-rain-toggle-with-thordfish";
	public static final String CONFIG_KEY_MONSOONDAY_RAIN_TOGGLE_OFF_WITH_THORDFISH_COST = "yearmarked-monsoonday-rain-toggle-off-with-thordfish-cost";
	public static final String CONFIG_KEY_MONSOONDAY_RAIN_TOGGLE_ON_WITH_THORDFISH_COST = "yearmarked-monsoonday-rain-toggle-on-with-thordfish-cost";

	// EARTHDAY
	public static final String CONFIG_KEY_EARTHDAY = "yearmarked-earthday";
	public static final String CONFIG_KEY_EARTHDAY_BONUS_CROPS = "yearmarked-earthday-bonus-crops";
	public static final String CONFIG_KEY_EARTHDAY_BONUS_CROPS_RANGE_HIGH = "yearmarked-earthday-bonus-crops-range-high";
	public static final String CONFIG_KEY_EARTHDAY_BONUS_CROPS_RANGE_LOW = "yearmarked-earthday-bonus-crops-range-low";

	// WORTAG 
	public static final String CONFIG_KEY_WORTAG = "yearmarked-wortag";
	public static final String CONFIG_KEY_WORTAG_BONUS_CROPS = "yearmarked-wortag-bonus-crops";
	public static final String CONFIG_KEY_WORTAG_BONUS_CROPS_RANGE_HIGH = "yearmarked-wortag-bonus-crops-range-high";
	public static final String CONFIG_KEY_WORTAG_BONUS_CROPS_RANGE_LOW = "yearmarked-wortag-bonus-crops-range-low";

	// DONNERSTAG
	public static final String CONFIG_KEY_DONNERSTAG = "yearmarked-donnerstag";
	public static final String CONFIG_KEY_DONNERSTAG_LIGHTNING = "yearmarked-donnerstag-lightning";
	public static final String CONFIG_KEY_DONNERSTAG_ALLOW_LIGHTNING_TOGGLE_WITH_THORDFISH = "yearmarked-donnerstag-allow-lightning-toggle-with-thordfish";
	public static final String CONFIG_KEY_DONNERSTAG_LIGHTNING_TOGGLE_OFF_WITH_THORDFISH_COST = "yearmarked-donnerstag-lightning-toggle-off-with-thordfish-cost";
	public static final String CONFIG_KEY_DONNERSTAG_LIGHTNING_TOGGLE_ON_WITH_THORDFISH_COST = "yearmarked-donnerstag-lightning-toggle-on-with-thordfish-cost";

	public static final String CONFIG_KEY_DONNERSTAG_ALLOW_SUPER_CREEPER_SPAWN_WITH_FISH_SWORD = "yearmarked-donnerstag-allow-super-creeper-spawn-with-fish-sword";
	public static final String CONFIG_KEY_DONNERSTAG_SUPER_CREEPER_SPAWN_WITH_FISH_SWORD_PERCENT_CHANCE = "yearmarked-donnerstag-super-creeper-spawn-with-fish-sword-percent-chance";
	public static final String CONFIG_KEY_DONNERSTAG_SUPER_CREEPER_SPAWN_DROPS_DIAMOND_PERCENT_CHANCE = "yearmarked-donnerstag-super-creeper-drops-diamond-percent-chance";
	public static final String CONFIG_KEY_DONNERSTAG_SUPER_CREEPER_SPAWN_DROPS_EMERALD_PERCENT_CHANCE = "yearmarked-donnerstag-super-creeper-drops-emerald-percent-chance";
	public static final String CONFIG_KEY_DONNERSTAG_SUPER_CREEPER_SPAWN_DROPS_THORDFISH_PERCENT_CHANCE = "yearmarked-donnerstag-super-creeper-drops-thordfish-percent-chance";
	public static final String CONFIG_KEY_DONNERSTAG_SUPER_CREEPER_OP_LUCK = "yearmarked-donnerstag-super-creeper-op-luck";

	// FISHFRYDAY
	public static final String CONFIG_KEY_FISHFRYDAY = "yearmarked-fishfryday";
	public static final String CONFIG_KEY_FISHFRYDAY_BINUS_XP_MULTIPLIER = "yearmarked-fishfryday-bonus-xp-multiplier";
	public static final String CONFIG_KEY_FISHFRYDAY_PERCENT_CHANCE_DIAMOND = "yearmarked-fishfryday-percent-chance-diamond";
	public static final String CONFIG_KEY_FISHFRYDAY_PERCENT_CHANCE_EMERALD = "yearmarked-fishfryday-percent-chance-emerald";
	public static final String CONFIG_KEY_FISHFRYDAY_PERCENT_CHANCE_GRASS = "yearmarked-fishfryday-percent-chance-grass";
	public static final String CONFIG_KEY_FISHFRYDAY_PERCENT_CHANCE_THORDFISH = "yearmarked-fishfryday-percent-chance-thordfish";
	public static final String CONFIG_KEY_FISHFRYDAY_OP_LUCK = "yearmarked-fishfryday-op-luck";

	// DIAMONDAY
	public static final String CONFIG_KEY_DIAMONDAY = "yearmarked-diamonday";
	public static final String CONFIG_KEY_DIAMONDAY_BONUS_DIAMONDS = "yearmarked-diamonday-bonus-diamonds";
	public static final String CONFIG_KEY_DIAMONDAY_BONUS_DIAMONDS_RANGE_HIGH = "yearmarked-diamonday-bonus-diamonds-range-high";
	public static final String CONFIG_KEY_DIAMONDAY_BONUS_DIAMONDS_RANGE_LOW = "yearmarked-diamonday-bonus-diamonds-range-low";

	// FEATHERSDAY
	public static final String CONFIG_KEY_FEATHERSDAY = "yearmarked-feathersday";
	public static final String CONFIG_KEY_FEATHERSDAY_PREVENT_FALL_DAMAGE = "yearmarked-feathersday-prevent-fall-damage";
	public static final String CONFIG_KEY_FEATHERSDAY_BOUNCE = "yearmarked-feathersday-bounce";
	public static final String CONFIG_KEY_FEATHERSDAY_BOUNCE_XZ_MULTIPLIER = "yearmarked-feathersday-bounce-xz-multiplier";

	// RECIPES
	public static final String RECIPE_THORDFISH = "recipe.thord-fish.name";
	public static final String RECIPE_FISH_SWORD = "recipe.fish-sword.name";
	public static final String RECIPE_FISH_ARROW = "recipe.fish-arrow.name";

	int checkTimeInterval = 5;// sec

	List<String> worlds;

	Set<String> playersExemptFromLightning = null;

	String nameOfThordfish = null;
	String nameOfFishSword = null;
	String nameOfFishArrow = null;

	
	public void onEnable() {
		super.onEnable();
		createConfig();

		getServer().getPluginManager().registerEvents(this, this);

		info("loaded worlds");
		worlds = getConfig().getStringList(CONFIG_KEY_WORLDS);
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

		nameOfThordfish = getConfig().getString(YearmarkedPlugin.RECIPE_THORDFISH);
		nameOfFishSword = getConfig().getString(YearmarkedPlugin.RECIPE_FISH_SWORD);
		nameOfFishArrow = getConfig().getString(YearmarkedPlugin.RECIPE_FISH_ARROW);

		startWorldMonitors();
		registerListeners();
		registerCommands();

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
				warning("General Exception while starting world moniror[" + world + "]. Message: " + e.getMessage());
				// e.printStackTrace();
				continue;
			}
		}
	}

	private void startWorldMonitor(String world) {
		info("Loading World" + ": " + world);
		TimeMonitor timeMonitor = new TimeMonitor(this, world);

		if (timeMonitor.getWorld() == null) {
			warning("   -" + world + " was no recognized");
			return;
		} else {
			info("   -" + world + " was loaded");
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

	private void registerCommands() {
		ChangeTimeCommand changeTimeCommand = new ChangeTimeCommand(this);
		this.getCommand("change-time").setExecutor(changeTimeCommand);
		this.getCommand("chtm").setExecutor(changeTimeCommand);
		getLogger().info("'/change-time' registered to ChangeTimeCommand");
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

	public String getNameOfThordfish() {
		return nameOfThordfish;
	}

	public String getNameOfFishSword() {
		return nameOfFishSword;
	}

	public String getNameOfFishArrow() {
		return nameOfFishArrow;
	}

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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	///////////////////////////
	// begin localization /////
	///////////////////////////
	
	public static String LOCALIZED_KEY_WORLD = "yearmarked.world";
	public static String LOCALIZED_KEY_BONUS = "yearmarked.bonus";
	public static String LOCALIZED_KEY_FALL_DAMAGE_PREVENTED = "yearmarked.fall.damage.prevented";
	public static String LOCALIZED_KEY_DOUBLE_XP = "yearmarked.double.xp";
	public static String LOCALIZED_KEY_FISH_HAD_DIAMOND = "yearmarked.fish.had.diamond";
	public static String LOCALIZED_KEY_FISH_HAD_EMERALD = "yearmarked.fish.had.emerald";
	public static String LOCALIZED_KEY_TODAY_IS = "yearmarked.today.is";

	public static String LOCALIZED_KEY_SUPER_CREEPER_HAD_DIAMOND = "yearmarked.super.creeper.had.diamond";
	public static String LOCALIZED_KEY_SUPER_CREEPER_HAD_EMERALD = "yearmarked.super.creeper.had.emerald";
	public static String LOCALIZED_KEY_SUPER_CREEPER_HAD_THORDFISH = "yearmarked.super.creeper.had.thordfish";

	public static String LOCALIZED_KEY_IT_IS_DAY_NUMBER = "yearmarked.it.is.day.number";
	public static String LOCALIZED_KEY_OF_MONTH_NUMBER = "yearmarked.of.month.number";
	public static String LOCALIZED_KEY_OF_YEAR_NUMBER = "yearmarked.of.year.number";
	
	public static String LOCALIZED_KEY_SUPER_CREEPER_HAD_A_DIAMOND = "yearmarked.super.creeper.had.diamond";
	public static String LOCALIZED_KEY_SUPER_CREEPER_HAD_AN_EMERALD = "yearmarked.super.creeper.had.emerald";
	public static String LOCALIZED_KEY_SUPER_CREEPER_HAD_A_THORDFISH = "yearmarked.super.creeper.had.thordfish";
	
	public static String LOCALIZED_KEY_TOGGLE_EFFECT_WITH_THORDFISH_CANT_AFFORD = "yearmarked.toggle.effect.with.thorfish.cant.afford";
	
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
