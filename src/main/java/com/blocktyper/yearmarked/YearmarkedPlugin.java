package com.blocktyper.yearmarked;

import java.io.File;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.ResourceBundle;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import com.blocktyper.localehelper.LocaleHelper;

public class YearmarkedPlugin extends JavaPlugin {

	public static Material DEFAULT_MONTH = Material.DIAMOND;
	public static String KEY_CURRENT_MONTH = "current-month";
	private String LOCALIZED_KEY_MONTH = "yearmarked.month";

	private LocaleHelper localeHelper = null;

	private String getLocalizedMessage(String key) {
		return localeHelper == null ? key : localeHelper.getLocalizedMessage(key);
	}

	public void onEnable() {
		createConfig();
		File pluginFile = getFile();
		localeHelper = new LocaleHelper(getLogger(), pluginFile != null ? pluginFile.getParentFile() : null,
				"YearmarkedMessages");
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

}
