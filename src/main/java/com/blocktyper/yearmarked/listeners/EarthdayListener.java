package com.blocktyper.yearmarked.listeners;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
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
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.blocktyper.yearmarked.ConfigKeyEnum;
import com.blocktyper.yearmarked.DayOfWeekEnum;
import com.blocktyper.yearmarked.LocalizedMessageEnum;
import com.blocktyper.yearmarked.YearmarkedCalendar;
import com.blocktyper.yearmarked.YearmarkedPlugin;


public class EarthdayListener extends AbstractListener {

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

	/**
	 * Drop the Earthday reward
	 * @param block
	 * @param rewardCount
	 */
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

	/**
	 * Eating an Earthday pot pie buffs the player with FAST_DIGGING, DAMAGE_RESISTANCE and SPEED for
	 * a time and with a magnitude specified in the config file with the keys 
	 * 'yearmarked-earthday-pot-pie-buff-duration-sec' and 'yearmarked-earthday-pot-pie-buff-magnitude' respectively.
	 * @param event
	 */
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

		int buffDuration = plugin.getConfig().getInt(ConfigKeyEnum.EARTHDAY_POT_PIE_BUFF_DURATION_SEC.getKey(), 30);
		int buffMagnitude = plugin.getConfig().getInt(ConfigKeyEnum.EARTHDAY_POT_PIE_BUFF_MAGNITUDE.getKey(), 5);

		event.getPlayer()
				.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, buffDuration * 20, buffMagnitude));

		event.getPlayer().addPotionEffect(
				new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, buffDuration * 20, buffMagnitude));

		event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, buffDuration * 20, buffMagnitude));

		setMetadata(event.getPlayer(), LAST_POT_PIE_TIME_KEY, new Date().getTime());

	}

	public void setMetadata(Entity player, String key, Object value) {
		player.setMetadata(key, new FixedMetadataValue(plugin, value));
	}

	
	/**
	 * When a player hits an entity with an Earthday crop, that animal will be converted into an entity arrow
	 * and the player will be charged a number of the Earthday crops specified in the config 
	 * using a string list of 'equals expressions' under the key 'yearmarked-earthday-entity-arrows-costs'
	 * The entity-arrow mechanic only works with specific combinations of crops and entities.
	 * If the player has recently eaten an Earthday pot pie, then the player can use any Earthday crop
	 *  on any entity with a cost defined under 'yearmarked-earthday-entity-arrows-costs'
	 * @param event
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void playerHitsAnimalWithEarthdayCrop(EntityDamageByEntityEvent event) {

		plugin.debugInfo("EntityDamageByEntityEvent - playerHitsAnimalWithEarthdayCrop");

		if (!worldEnabled(event.getDamager().getWorld().getName(), "earth-day-pot-pie")) {
			plugin.debugInfo("earth-day-pot-pie not enabled.");
			return;
		}
		
		if (event.getCause() != null && event.getCause().equals(DamageCause.PROJECTILE))
			return;

		Player player = null;

		if (!(event.getDamager() instanceof Player)) {
			plugin.debugInfo("[playerHitsAnimalWithEarthdayCrop] EntityDamageByEntityEvent - Not a player");
		} else {
			player = (Player) event.getDamager();
		}

		ItemStack itemInHand = plugin.getPlayerHelper().getItemInHand(player);

		if (itemInHand == null) {
			plugin.debugInfo("[playerHitsAnimalWithEarthdayCrop] EntityDamageByEntityEvent - No item in hand");
			return;
		}

		if (itemInHand.getItemMeta() == null || itemInHand.getItemMeta().getDisplayName() == null) {
			plugin.debugInfo("[playerHitsAnimalWithEarthdayCrop] EntityDamageByEntityEvent - No named item in hand");
			return;
		}

		if (!itemInHand.getItemMeta().getDisplayName()
				.startsWith(plugin.getConfig().getString(ConfigKeyEnum.EARTHDAY.getKey()))) {
			plugin.debugInfo("[playerHitsAnimalWithEarthdayCrop] EntityDamageByEntityEvent - No Earthday item in hand");
			return;
		}

		List<MetadataValue> lastPotPieTimeMetaList = player.getMetadata(LAST_POT_PIE_TIME_KEY);
		boolean userHasPotPieBuff = false;

		if (lastPotPieTimeMetaList != null) {
			Date lastPotPieTimeDate = null;

			try {
				
				long timePieWasLastEatenMS = lastPotPieTimeMetaList.get(0).asLong();
				
				lastPotPieTimeDate = new Date(timePieWasLastEatenMS);
				
				int lastPotPieTimeLimitSec = plugin.getConfig()
						.getInt(ConfigKeyEnum.EARTHDAY_POT_PIE_AFFECT_ARROWS_DURATION_SEC.getKey(), 15);
				if (lastPotPieTimeDate != null
						&& (new Date().getTime() - lastPotPieTimeDate.getTime()) / 1000 < lastPotPieTimeLimitSec) {
					userHasPotPieBuff = true;
					plugin.debugInfo("userHasPotPieBuff");
				} else {
					plugin.debugInfo("expired pot pie buff time");
				}
			} catch (Exception e) {
				plugin.debugWarning("Error determining if user has pot pie buff. Message: " + e.getMessage());
			}
		} else {
			plugin.debugInfo("playerHitsAnimalWithEarthdayCrop - no pot pie buff time");
		}

		if (!userHasPotPieBuff && !ANIMAL_ARROW_TYPES.contains(event.getEntity().getType())) {
			plugin.debugInfo("playerHitsAnimal - not an earthday arrow animal");
			return;
		}

		if (itemInHand.getType().equals(Material.POTATO_ITEM)) {
			if (userHasPotPieBuff || (event.getEntity().getType().equals(EntityType.RABBIT)
					|| event.getEntity().getType().equals(EntityType.CHICKEN))) {
				replaceEntityWithEntityArrow(event.getEntity(), itemInHand, player);
			}
		} else if (itemInHand.getType().equals(Material.WHEAT)) {
			if (userHasPotPieBuff || (event.getEntity().getType().equals(EntityType.COW)
					|| event.getEntity().getType().equals(EntityType.SHEEP))) {
				replaceEntityWithEntityArrow(event.getEntity(), itemInHand, player);
			}
		} else if (itemInHand.getType().equals(Material.CARROT_ITEM)) {
			if (userHasPotPieBuff || (event.getEntity().getType().equals(EntityType.PIG)
					|| event.getEntity().getType().equals(EntityType.HORSE))) {
				replaceEntityWithEntityArrow(event.getEntity(), itemInHand, player);
			}
		} else {
			plugin.debugInfo("playerHitsAnimalWithEarthdayCrop - not an earthday crop");
		}

	}

	/**
	 * Replaces entity with an arrow.  Charges the player a number of of the itemInHand determined by 
	 * costs specified in the config using a string list of 'equals expressions' under 
	 * the key 'yearmarked-earthday-entity-arrows-costs' 
	 * 
	 * @param entity
	 * @param itemInHand
	 * @param player
	 */
	private void replaceEntityWithEntityArrow(Entity entity, ItemStack itemInHand, Player player) {

		plugin.debugInfo("replaceEntityWithEntityArrow - Entity type: " + entity.getType());
		//equals expressions for each entity type and the number of the itemInHand which will be 
		//charged to convert the entity into an entity arrow
		List<String> costs = plugin.getConfig()
				.getStringList(ConfigKeyEnum.EARTHDAY_ALLOW_ENTITY_ARROWS_COSTS.getKey());

		Integer cost = null;

		if (costs != null) {
			for (String costEqualsExpression : costs) {
				String entityEqualsPrefix = entity.getType().toString() + "=";
				if (costEqualsExpression.startsWith(entityEqualsPrefix)
						&& costEqualsExpression.length() > entityEqualsPrefix.length()) {
					try {
						cost = Integer.parseInt(costEqualsExpression.substring(
								costEqualsExpression.indexOf(entityEqualsPrefix) + entityEqualsPrefix.length()));
						break;
					} catch (NumberFormatException e) {
						plugin.warning("Unable to parse cost of entity arrow: " + costEqualsExpression);
						return;
					}
				}
			}
		}

		//this only supports charging a player up to the max stack size for the given material
		if (cost == null || cost < 0) {
			plugin.debugInfo("replaceEntityWithEntityArrow - missing or negative cost found for entity type: " + entity.getType());
			return;
		} else if (cost == 0) {
			plugin.debugInfo("replaceEntityWithEntityArrow - no cost for entity arrow type: " + entity.getType());
		} else if (itemInHand.getAmount() == cost) {
			plugin.debugInfo("replaceEntityWithEntityArrow - exact payment ["+cost+"] for entity arrow type: " + entity.getType());
			player.getInventory().remove(itemInHand);
		} else if (itemInHand.getAmount() < cost) {
			plugin.debugInfo("replaceEntityWithEntityArrow - cant afford ["+cost+"] entity arrow type: " + entity.getType());
			String missingPayment = "";
			for (int i = 0; i < cost - itemInHand.getAmount(); i++) {
				missingPayment += "$";
			}
			player.sendMessage(ChatColor.RED + missingPayment);
			return;
		} else if (itemInHand.getAmount() > cost) {
			plugin.debugInfo("replaceEntityWithEntityArrow - more than enough ["+cost+"] for entity arrow type: " + entity.getType());
			itemInHand.setAmount(itemInHand.getAmount() - cost);
		} else {
			plugin.warning("replaceEntityWithEntityArrow - Unexpected use case[ItemInHand: " + itemInHand.getAmount()
					+ " - cost: " + cost + "]");
			return;
		}

		//create arrow for entity
		ItemStack arrow = new ItemStack(Material.ARROW);
		ItemMeta meta = arrow.getItemMeta();
		meta.setDisplayName(plugin.getConfig().getString(DayOfWeekEnum.EARTHDAY.getDisplayKey()) + " "
				+ entity.getType().toString());
		arrow.setItemMeta(meta);
		
		//drop the arrow
		entity.getWorld().dropItem(entity.getLocation(), arrow);
		
		//remove the entity
		entity.remove();

		plugin.debugInfo("replaceEntityWithEntityArrow - arrow dropped");
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onEntityArrowHit(ProjectileHitEvent event) {

		if (!(event.getEntity().getShooter() instanceof Player))
			return;

		if (event.getEntity() == null || event.getEntity().getCustomName() == null){
			plugin.debugInfo("onEntityArrowHit - not a named arrow");
			return;
		}

		if (!event.getEntity().getCustomName()
				.startsWith(plugin.getConfig().getString(ConfigKeyEnum.EARTHDAY.getKey()))) {
			plugin.debugInfo("onEntityArrowHit - not an earthday arrow");
			return;
		}

		String entityName = event.getEntity().getCustomName()
				.replace(plugin.getConfig().getString(ConfigKeyEnum.EARTHDAY.getKey()), "").trim();

		EntityType entityType = EntityType.valueOf(entityName);

		if (entityType == null) {
			plugin.debugInfo("onEntityArrowHit - not an entity arrow");
			return;
		}

		event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), entityType);

		plugin.debugInfo("onEntityArrowHit - animal spawned");

		event.getEntity().remove();
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void opPlayerShootEntityArrow(EntityShootBowEvent event) {

		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();

		ItemStack firstArrowStack = plugin.getPlayerHelper().getFirstArrowStack(player);

		if (firstArrowStack == null || firstArrowStack.getItemMeta() == null
				|| firstArrowStack.getItemMeta().getDisplayName() == null) {
			plugin.debugInfo("opPlayerShootEntityArrow - not a named arrow");
			return;
		}

		if (!firstArrowStack.getItemMeta().getDisplayName()
				.startsWith(plugin.getConfig().getString(DayOfWeekEnum.EARTHDAY.getDisplayKey()))) {
			plugin.debugInfo("opPlayerShootEntityArrow - not an earthday arrow");
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
