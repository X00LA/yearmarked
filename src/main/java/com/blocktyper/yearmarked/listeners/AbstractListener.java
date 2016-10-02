package com.blocktyper.yearmarked.listeners;

import org.bukkit.event.Listener;

import com.blocktyper.yearmarked.YearmarkedPlugin;

public class AbstractListener implements Listener {
	protected YearmarkedPlugin plugin;

	public AbstractListener(YearmarkedPlugin plugin) {
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	protected void warning(String msg) {
		plugin.getLogger().warning(msg);
	}

	protected void info(String msg) {
		plugin.getLogger().info(msg);
	}

}
