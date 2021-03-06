package com.blocktyper.yearmarked.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.blocktyper.yearmarked.ConfigKeyEnum;
import com.blocktyper.yearmarked.LocalizedMessageEnum;
import com.blocktyper.yearmarked.YearmarkedPlugin;

public class SuperCreeperDamageListener extends AbstractListener {

	public SuperCreeperDamageListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void entityShootBow(EntityShootBowEvent event) {

		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();
		
		if (!worldEnabled(player.getWorld().getName(), plugin.getNameOfFishArrow())) {
			return;
		}

		ItemStack firstArrowStack = plugin.getPlayerHelper().getFirstArrowStack(player);

		if (firstArrowStack != null) {
			plugin.debugInfo("arrow stack located. size: " + firstArrowStack.getAmount());

			if (firstArrowStack.getItemMeta() == null || firstArrowStack.getItemMeta().getDisplayName() == null) {
				plugin.debugInfo("arrows have no display name");
				return;
			}
			
			ItemStack bow = plugin.getPlayerHelper().getItemInHand(player);
			if(plugin.getPlayerHelper().itemHasEnchantment(bow, Enchantment.ARROW_INFINITE)){
				plugin.debugInfo("Infinite enchantment not approved.");
			}else{
				// name it whatever the item stack is named
				// we will worry about if it is configured in the
				// EntityDamageByEntityEvent handler playerKillSuperCreeper
				event.getProjectile().setCustomName(firstArrowStack.getItemMeta().getDisplayName());
			}
			
		} else {
			plugin.debugInfo("no arrows found");
		}

	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void playerKillSuperCreeper(EntityDamageByEntityEvent event) {

		if (!worldEnabled(event.getDamager().getWorld().getName(), "Super Creeper hit")) {
			return;
		}

		Player player = null;
		boolean fishArrowDamage = false;

		if (!(event.getDamager() instanceof Player)) {

			if (plugin.getNameOfFishArrow() == null) {
				return;
			} else {
				if (event.getCause().equals(DamageCause.PROJECTILE)) {

					if (event.getDamager() instanceof Projectile
							&& plugin.getNameOfFishArrow().equals(((Projectile) event.getDamager()).getCustomName())) {
						fishArrowDamage = true;
						plugin.debugInfo("[playerKillSuperCreeper] damage from:" + plugin.getNameOfFishArrow());

						if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
							player = (Player) ((Projectile) event.getDamager()).getShooter();
						}
					}
				}

				if (!fishArrowDamage) {
					return;
				}
			}

		} else {
			player = (Player) event.getDamager();
		}

		if (!(event.getEntity() instanceof Creeper)) {
			return;
		}

		Creeper creeper = (Creeper) event.getEntity();
		if (!creeper.isPowered()) {
			return;
		}

		if (!fishArrowDamage) {
			ItemStack itemInHand = plugin.getPlayerHelper().getItemInHand(player);

			if (itemInHand == null) {
				return;
			}

			if (itemInHand.getItemMeta() == null || itemInHand.getItemMeta().getDisplayName() == null) {
				return;
			}
		}

		if (event.getFinalDamage() < creeper.getHealth()) {
			return;
		}

		boolean isOpLucky = player.isOp()
				&& plugin.getConfig().getBoolean(ConfigKeyEnum.DONNERSTAG_SUPER_CREEPER_OP_LUCK.getKey(), true);

		if(isOpLucky)
			player.sendMessage(ChatColor.GOLD + "OP!");

		double dropDiamondPercent = plugin.getConfig()
				.getDouble(ConfigKeyEnum.DONNERSTAG_SUPER_CREEPER_SPAWN_DROPS_DIAMOND_PERCENT_CHANCE.getKey(), 5);
		double dropEmeraldPercent = plugin.getConfig()
				.getDouble(ConfigKeyEnum.DONNERSTAG_SUPER_CREEPER_SPAWN_DROPS_EMERALD_PERCENT_CHANCE.getKey(), 10);
		double dropThordfishPercent = plugin.getConfig()
				.getDouble(ConfigKeyEnum.DONNERSTAG_SUPER_CREEPER_SPAWN_DROPS_THORDFISH_PERCENT_CHANCE.getKey(), 15);

		// boolean spawnCreeper = plugin.rollTrueOrFalse(dropDiamondPercent);

		if (isOpLucky || plugin.rollIsLucky(dropDiamondPercent)) {
			Material reward = Material.DIAMOND;
			String message = plugin.getLocalizedMessage(LocalizedMessageEnum.SUPER_CREEPER_HAD_DIAMOND.getKey(), player);
			ChatColor color = ChatColor.BLUE;
			doReward(creeper, player, reward, message, color);
		}
		if (isOpLucky || plugin.rollIsLucky(dropEmeraldPercent)) {
			Material reward = Material.EMERALD;
			String message = plugin.getLocalizedMessage(LocalizedMessageEnum.SUPER_CREEPER_HAD_EMERALD.getKey(), player);
			ChatColor color = ChatColor.GREEN;
			doReward(creeper, player, reward, message, color);
		}
		if (isOpLucky || plugin.rollIsLucky(dropThordfishPercent)) {
			if (plugin.getNameOfThordfish() != null) {
				String message = String.format(
						plugin.getLocalizedMessage(LocalizedMessageEnum.SUPER_CREEPER_HAD_THORDFISH.getKey(), player),
						plugin.getNameOfThordfish());
				doReward(creeper, player, Material.RAW_FISH, message, ChatColor.DARK_GREEN,
						plugin.getNameOfThordfish());
			} else {
				plugin.debugInfo("[playerKillSuperCreeper] no thordfish info for super creeper to drop one");
			}
		}
	}

	private void doReward(Creeper creeper, Player player, Material reward, String message, ChatColor color) {
		doReward(creeper, player, reward, message, color, null);
	}

	private void doReward(Creeper creeper, Player player, Material reward, String message, ChatColor color,
			String customName) {
		if (reward != null) {
			ItemStack item = new ItemStack(reward);

			if (customName != null) {
				ItemMeta itemMeta = item.getItemMeta();
				if (itemMeta != null) {
					itemMeta.setDisplayName(customName);
					item.setItemMeta(itemMeta);
				} else {
					plugin.debugWarning("Could not set custom name for loot dropped by super creeper: " + customName);
				}
			}

			player.getWorld().dropItem(creeper.getLocation(), item);
			if (message != null)
				player.sendMessage(color + message);
		}
	}

}
