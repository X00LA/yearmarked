package com.blocktyper.yearmarked.listeners;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Crops;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.blocktyper.yearmarked.ConfigKeyEnum;
import com.blocktyper.yearmarked.DayOfWeekEnum;
import com.blocktyper.yearmarked.LocalizedMessageEnum;
import com.blocktyper.yearmarked.YearmarkedCalendar;
import com.blocktyper.yearmarked.YearmarkedPlugin;

import net.md_5.bungee.api.ChatColor;

public class EarthdayListener extends AbstractListener {

	public static final int LAST_POT_PIE_TIME_TIME_LIMIT = 30;
	public static final String LAST_POT_PIE_TIME_KEY = "last-pot-pie-time";

	public static final List<EntityType> ANIMAL_ARROW_TYPES = Arrays.asList(EntityType.COW, EntityType.HORSE,
			EntityType.CHICKEN, EntityType.SHEEP, EntityType.PIG, EntityType.SHEEP, EntityType.RABBIT);

	private Random random = new Random();

	public EarthdayListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onCropsBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();

		if (block.getType() != Material.CROPS && block.getType() != Material.CARROT
				&& block.getType() != Material.POTATO) {
			return;
		}

		YearmarkedCalendar cal = new YearmarkedCalendar(block.getWorld());
		if (!cal.getDayOfWeekEnum().equals(DayOfWeekEnum.EARTHDAY)) {
			return;
		}

		if (!plugin.getConfig().getBoolean(ConfigKeyEnum.EARTHDAY_BONUS_CROPS.getKey(), true)) {
			plugin.debugInfo(ConfigKeyEnum.EARTHDAY_BONUS_CROPS.getKey() + ": false");
			return;
		}

		if (block.getType() != Material.CROPS && block.getType() != Material.CARROT
				&& block.getType() != Material.POTATO) {
			return;
		}

		if (((Crops) block.getState().getData()).getState() != CropState.RIPE) {
			plugin.debugInfo("Ripe " + block.getType() + " on earthday.");
			return;
		}

		if (!worldEnabled(block.getWorld().getName(),
				plugin.getConfig().getString(DayOfWeekEnum.EARTHDAY.getDisplayKey()))) {
			return;
		}

		int high = plugin.getConfig().getInt(ConfigKeyEnum.EARTHDAY_BONUS_CROPS_RANGE_HIGH.getKey(), 3);
		int low = plugin.getConfig().getInt(ConfigKeyEnum.EARTHDAY_BONUS_CROPS_RANGE_LOW.getKey(), 1);

		int rewardCount = random.nextInt(high + 1);

		if (rewardCount < low) {
			rewardCount = low;
		}

		if (rewardCount > 0) {
			String bonus = plugin.getLocalizedMessage(LocalizedMessageEnum.BONUS.getKey());
			event.getPlayer()
					.sendMessage(ChatColor.DARK_GREEN + bonus + "[x" + rewardCount + "] " + block.getType().toString());
			reward(block, rewardCount);
		} else {
			plugin.debugInfo("No luck on Earthday");
			event.getPlayer().sendMessage(ChatColor.RED + ":(");
		}

	}

	private void reward(Block block, int rewardCount) {

		Material reward = Material.WHEAT;
		if (block.getType() == Material.CROPS) {
			reward = Material.WHEAT;
		} else if (block.getType() == Material.CARROT) {
			reward = Material.CARROT_ITEM;
		} else if (block.getType() == Material.POTATO) {
			reward = Material.POTATO_ITEM;
		} else {
			reward = Material.GRASS;
		}

		dropItemsInStacks(block.getLocation(), reward, rewardCount,
				plugin.getConfig().getString(ConfigKeyEnum.EARTHDAY.getKey()) + " " + reward.name());
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPotPieEat(PlayerItemConsumeEvent event) {

		if (plugin.getNameOfEarthdayPotPie() == null)
			return;

		YearmarkedCalendar cal = new YearmarkedCalendar(event.getPlayer().getWorld());
		if (!cal.getDayOfWeekEnum().equals(DayOfWeekEnum.EARTHDAY)) {
			return;
		}

		ItemMeta meta = event.getItem().getItemMeta();
		if (meta == null || meta.getDisplayName() == null)
			return;

		if (!meta.getDisplayName().equals(plugin.getNameOfEarthdayPotPie()))
			return;

		event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 14 * 20, 5));

		event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 14 * 20, 5));

		event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 14 * 20, 5));

		setMetadata(event.getPlayer(), LAST_POT_PIE_TIME_KEY, new Date());

	}

	public void setMetadata(Entity player, String key, Object value) {
		player.setMetadata(key, new FixedMetadataValue(plugin, value));
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void playerHitsAnimal(EntityDamageByEntityEvent event) {

		plugin.debugInfo("EntityDamageByEntityEvent");

		if (!worldEnabled(event.getDamager().getWorld().getName(), "earth-day-pot-pie")) {
			plugin.debugInfo("earth-day-pot-pie not enabled.");
			return;
		}

		Player player = null;

		if (!(event.getDamager() instanceof Player)) {
			plugin.debugInfo("[playerHitsAnimal] EntityDamageByEntityEvent - Not a player");

			if (event.getCause().equals(DamageCause.PROJECTILE))
				return;
		} else {
			player = (Player) event.getDamager();
		}

		ItemStack itemInHand = plugin.getPlayerHelper().getItemInHand(player);

		if (itemInHand == null) {
			plugin.debugInfo("[playerHitsAnimal] EntityDamageByEntityEvent - No item in hand");
			return;
		}

		if (itemInHand.getItemMeta() == null || itemInHand.getItemMeta().getDisplayName() == null) {
			plugin.debugInfo("[playerHitsAnimal] EntityDamageByEntityEvent - No named item in hand");
			return;
		}

		if (!itemInHand.getItemMeta().getDisplayName()
				.startsWith(plugin.getConfig().getString(ConfigKeyEnum.EARTHDAY.getKey()))) {
			plugin.debugInfo("[playerHitsAnimal] EntityDamageByEntityEvent - No Earthday item in hand");
			return;
		}

		if (!ANIMAL_ARROW_TYPES.contains(event.getEntity().getType())) {
			plugin.debugInfo("playerHitsAnimal - not an earthday arrow animal");
			return;
		}

		Object lastPotPieTimeObj = player.getMetadata(LAST_POT_PIE_TIME_KEY);
		boolean userHasPotPieBuff = false;

		if (lastPotPieTimeObj != null) {
			Date lastPotPieTimeDate = null;

			try {
				lastPotPieTimeDate = (Date) lastPotPieTimeObj;
				if (lastPotPieTimeDate != null && (new Date().getTime() - lastPotPieTimeDate.getTime())
						/ 1000 < LAST_POT_PIE_TIME_TIME_LIMIT) {
					userHasPotPieBuff = true;
				} else {
					plugin.debugInfo("expired pot pie buff time");
				}
			} catch (Exception e) {
				plugin.debugWarning("Error determining if user has pot pie buff. Message: " + e.getMessage());
			}
		} else {
			plugin.debugInfo("playerHitsAnimal - no pot pie buff time");
		}

		if (itemInHand.getType().equals(Material.POTATO_ITEM)) {
			if (userHasPotPieBuff || (event.getEntity().getType().equals(EntityType.RABBIT) || event.getEntity().getType().equals(EntityType.CHICKEN))) {
				dropArrowForAnimal(event.getEntity(), itemInHand, player);
			}
		} else if (itemInHand.getType().equals(Material.WHEAT)) {
			if (userHasPotPieBuff || (event.getEntity().getType().equals(EntityType.COW) || event.getEntity().getType().equals(EntityType.SHEEP))) {
				dropArrowForAnimal(event.getEntity(), itemInHand, player);
			}
		} else if (itemInHand.getType().equals(Material.CARROT_ITEM)) {
			if (userHasPotPieBuff || (event.getEntity().getType().equals(EntityType.PIG)
					|| event.getEntity().getType().equals(EntityType.HORSE))) {
				dropArrowForAnimal(event.getEntity(), itemInHand, player);
			}
		}else{
			plugin.debugInfo("playerHitsAnimal - not an earthday crop");
		}

	}

	private void dropArrowForAnimal(Entity entity, ItemStack itemInHand, Player player) {
		ItemStack arrow = new ItemStack(Material.ARROW);
		ItemMeta meta = arrow.getItemMeta();
		meta.setDisplayName(plugin.getConfig().getString(DayOfWeekEnum.EARTHDAY.getDisplayKey()) + " "
				+ entity.getType().toString());
		arrow.setItemMeta(meta);
		entity.getWorld().dropItem(entity.getLocation(), arrow);
		entity.remove();
		
		plugin.debugInfo("dropArrowForAnimal - arrow dropped");

		if(itemInHand.getAmount() == 1){
			player.getInventory().remove(itemInHand);
		}else{
			itemInHand.setAmount(itemInHand.getAmount() - 1);
		}
		
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onProjectileHit(ProjectileHitEvent event) {

		if (!(event.getEntity().getShooter() instanceof Player))
			return;

		if (event.getEntity() == null || event.getEntity().getCustomName() == null)
			return;

		if (!event.getEntity().getCustomName()
				.startsWith(plugin.getConfig().getString(ConfigKeyEnum.EARTHDAY.getKey()))){
			plugin.debugInfo("onProjectileHit - not an earthday arrow");
			return;
		}

		String entityName = event.getEntity().getCustomName()
				.replace(plugin.getConfig().getString(ConfigKeyEnum.EARTHDAY.getKey()), "").trim();

		EntityType entityType = EntityType.valueOf(entityName);

		if (entityType == null){
			plugin.debugInfo("onProjectileHit - not an entity arrow");
			return;
		}

		Entity spawnedEntity = event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), entityType);

		if (!ANIMAL_ARROW_TYPES.contains(spawnedEntity.getType())) {
			plugin.debugInfo("onProjectileHit - not an earthday arrow animal");
			spawnedEntity.remove();
			return;
		}
		
		plugin.debugInfo("onProjectileHit - animal spawned");

		event.getEntity().remove();
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void entityShootBow(EntityShootBowEvent event) {

		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();

		ItemStack firstArrowStack = plugin.getPlayerHelper().getFirstArrowStack(player);

		if (firstArrowStack == null || firstArrowStack.getItemMeta() == null
				|| firstArrowStack.getItemMeta().getDisplayName() == null) {
			plugin.debugInfo("entityShootBow - not a named arrow");
			return;
		}

		if (!firstArrowStack.getItemMeta().getDisplayName()
				.startsWith(plugin.getConfig().getString(DayOfWeekEnum.EARTHDAY.getDisplayKey()))){
			plugin.debugInfo("entityShootBow - not an earthday arrow");
			return;
		}

		event.getProjectile().setCustomName(firstArrowStack.getItemMeta().getDisplayName());
	}

}

/*
 * 
 * 
 * 
 */
