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

	public void onEnable() {
		createConfig();
		resourceName = "com.blocktyper.yearmarked.resources.YearmarkedMessages";
		locale = new LocaleHelper(getLogger(), getFile() != null ? getFile().getParentFile() : null).getLocale();
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
			getLogger().warning("Unexpected error getting localized string for key(" + key + "). Message: " + e.getMessage());
		}
		return value;
	}

	// end localization

}
