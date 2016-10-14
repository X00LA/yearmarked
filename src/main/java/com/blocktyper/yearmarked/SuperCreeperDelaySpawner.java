package com.blocktyper.yearmarked;

import java.text.MessageFormat;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

public class SuperCreeperDelaySpawner extends BukkitRunnable{
	
	private YearmarkedPlugin plugin = null;
	private World world;
	private Location location;
	
	public SuperCreeperDelaySpawner(YearmarkedPlugin plugin, World world, Location location) {
		super();
		this.plugin = plugin;
		this.world = world;
		this.location = location;
	}
	
	public void run() {
		if(!plugin.worldEnabled(world.getName())){
			plugin.debugInfo("no spawn. world not enabled.");
			return;
		}

		String message = new MessageFormat("Spawning zombie in world {0} a ({1},{2},{3})").format(new Object[]{world.getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()});
		plugin.debugInfo(message);
		Creeper creeper = (Creeper)world.spawnEntity(location, EntityType.CREEPER);
		creeper.setPowered(true);
		
	}

}
