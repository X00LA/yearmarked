package com.blocktyper.yearmarked;

import java.io.File;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.ResourceBundle;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class YearmarkedPlugin extends JavaPlugin {

	public static Material DEFAULT_MONTH = Material.DIAMOND;
	public static String KEY_CURRENT_MONTH = "current-month";

	public void onEnable() {
		createConfig();

		String currentMonth = getConfig().getString(KEY_CURRENT_MONTH);
		getLogger().info(getLocalizedMessage(LOCALIZED_KEY_MONTH) + ": " + currentMonth);

	}

	// begin config file initialization
	private void createConfig() {
		getLogger().info("Loading up Config.yml");
		try {
			if (!getDataFolder().exists()) {
				getDataFolder().mkdirs();
			}

			File file = new File(getDataFolder(), "config.yml");
			if (!file.exists()) {
				getLogger().info("Config.yml not found, creating");
				PrintWriter writer = new PrintWriter(file.getAbsolutePath(), "UTF-8");
				writer.println(KEY_CURRENT_MONTH + ": " + DEFAULT_MONTH.toString());
				writer.close();
			} else {
				getLogger().info("Config.yml found, loading");
			}
			getLogger().info("Done loading up Config.yml");
		} catch (Exception e) {
			getLogger().warning("Error loading Config.yml: " + e.getMessage());
		}

	}
	// end config file initialization

	// begin localization
	private String LOCALIZED_KEY_MONTH = "yearmarked.month";
	private Locale defaultLocale = Locale.getDefault();
	private ResourceBundle bundle = null;
	private boolean bundleLoadFailed = false;

	private String getLocalizedMessage(String key) {
		
		if(bundleLoadFailed){
			return key;
		}
		
		if(bundle == null){
			try {
				bundle = ResourceBundle.getBundle("Messages", defaultLocale);
			} catch (Exception e) {
				getLogger().warning("Messages bundle did not load successfully from default location.");
			}
			if(bundle == null){
				getLogger().info("Checking for Messages bundle in secondary location (resources/Messages).");
				try {
					bundle = ResourceBundle.getBundle("resources/Messages", defaultLocale);
				} catch (Exception e) {
					getLogger().warning("Messages bundle did not load successfully from secondary location (resources/Messages).");
				}
				
				if(bundle == null){
					getLogger().warning("Messages will appear as dot separated key names.  Please remove this plugin from your plugin folder if this behaviour is not desired.");
					bundleLoadFailed = true;
					return key;
				}else{
					getLogger().info("Messages bundle loaded successfully from secondary location (resources/Messages).");
				}
			}else{
				getLogger().info("Messages bundle loaded successfully from default location.");
			}
		}
		
		
		String value = bundle.getString(key);
		try {
			value = key != null ? (value != null && !value.trim().isEmpty() ? value : key) : "null key";
		} catch (Exception e) {
			value = "error value";
		}
		return value;
	}
	// end localization
}
