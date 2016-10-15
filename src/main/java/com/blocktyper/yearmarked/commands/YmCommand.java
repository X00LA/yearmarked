package com.blocktyper.yearmarked.commands;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.avaje.ebeaninternal.server.persist.BindValues.Value;
import com.blocktyper.yearmarked.LocalizedMessageEnum;
import com.blocktyper.yearmarked.YearmarkedCalendar;
import com.blocktyper.yearmarked.YearmarkedPlugin;

import net.md_5.bungee.api.ChatColor;

public class YmCommand implements CommandExecutor {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	private YearmarkedPlugin plugin;
	
	Map<String,Map<String,Long>> playerWorldReturnMap;

	public YmCommand(YearmarkedPlugin plugin) {
		this.plugin = plugin;
		playerWorldReturnMap = new HashMap<String, Map<String,Long>>();
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// the default mode is to treat the first argument as a number or a
		// number surrounded by parenthesis
		// control flow is passed to handleAlternateFirstArgument the first
		// argument is not a number or
		// a number surrounded by parenthesis
		try {
			if (!(sender instanceof Player)) {
				return false;
			}

			Player player = (Player) sender;

			if (!plugin.worldEnabled(player.getWorld().getName())) {
				plugin.debugInfo("no time commands. world not enabled.");
				String message = plugin.getLocalizedMessage(LocalizedMessageEnum.WORLD_NOT_ENABLED.getKey());
				player.sendMessage(new MessageFormat(message).format(new Object[] { player.getWorld().getName() }));
				return false;
			}

			if (!player.isOp()) {
				String message = plugin.getLocalizedMessage(LocalizedMessageEnum.ONLY_FOR_OP.getKey());
				player.sendMessage(message);
				return true;
			}

			double daysToProgress = 1.0;
			boolean isRelative = false;

			if (args != null && args.length > 0) {

				String firstArg = args[0];

				if (firstArg != null) {

					if (firstArg.startsWith("(") && firstArg.endsWith(")")) {
						isRelative = true;
						firstArg = firstArg.substring(1, firstArg.length() - 1);
					}

					try {
						daysToProgress = Double.parseDouble(firstArg);
					} catch (Exception e) {
						return handleAlternateFirstArgument(args, player);
					}

				}
			} else {
				return handleNoArgs(player);
			}

			int ticksInDay = 24000;

			Double valueToProgress = daysToProgress * ticksInDay;

			if (isRelative) {

				if (player.getWorld().getFullTime() < ticksInDay) {
					if (valueToProgress < 0) {
						valueToProgress = player.getWorld().getFullTime() * -1.0;
					} else {
						valueToProgress = valueToProgress - player.getWorld().getFullTime();
					}
				} else {
					double remainder = player.getWorld().getFullTime() % ticksInDay;
					valueToProgress = valueToProgress - remainder;
				}
			}

			Long fullTime = player.getWorld().getFullTime() + Long.valueOf(valueToProgress.longValue());

			if (fullTime < 0) {
				fullTime = 0L;
			}

			player.getWorld().setFullTime(fullTime);

			player.sendMessage("/time set " + fullTime);

			return true;
		} catch (Exception e) {
			plugin.warning("error during change-time command");
			return false;
		}

	}

	private boolean handleNoArgs(Player player) {
		player.sendMessage(ChatColor.GREEN + "#----------------");
		player.sendMessage(ChatColor.GREEN + "#----------------");
		YearmarkedCalendar cal = new YearmarkedCalendar(player.getWorld().getFullTime());
		plugin.sendDayInfo(cal, Arrays.asList(player));
		player.sendMessage(ChatColor.GREEN + "#----------------");
		player.sendMessage(ChatColor.GREEN + "#----------------");
		player.sendMessage(ChatColor.GREEN + "commands: ");
		player.sendMessage("  - /ym 3 					#Moves world's fulltime forward exactly 24000x3 units");
		player.sendMessage("  - /ym -1 					#Moves world's fulltime backwards exactly 24000x1 units");
		player.sendMessage("  - /ym (5) 				#Moves world's fulltime forward to the exact start of the day 5 from *now");
		player.sendMessage("  - /ym (-2) 				#Moves world's fulltime backwards to the exact start of the day 2 *ago");
		player.sendMessage("  - /ym date yyy-mm-dd 		#Moves to the date you specify in the Yearmarked calendar");
		player.sendMessage("  - /ym day 1				#Moves to day one through 7. Before you change the time, your current fulltime is stored in server RAM and allows you to toggle back where you were before running the command (see '/ym return')");
		player.sendMessage("  - /ym return				#Moves the world's full time to where the user was before they ran the 'ym day' command or the 'ym date' command.");

		return false;
	}

	private boolean handleAlternateFirstArgument(String[] args, Player player) {
		if(args == null || args.length < 1 ||  args[0] == null){
			plugin.debugInfo("Null or empty args");
			return false;
		}
		
		if (args[0].equals("date")) {
			plugin.debugInfo("'date' 1st arg");
			return handleDateArgument(args, player);
		} else if (args[0].equals("day")) {
			plugin.debugInfo("'day' 1st arg");
			return handleDayArgument(args, player);
		} else if (args[0].equals("return")) {
			plugin.debugInfo("'return' 1st arg");
			return handleReturnArgument(args, player);
		}
		return false;
	}
	
	private boolean validateSecondArg(String[] args, Player player){
		if(args.length < 2 || args[1] == null){
			plugin.debugInfo("no 2nd arg");
			return false;
		}
		
		return true;
	}

	private boolean handleDateArgument(String[] args, Player player) {
		
		if(!validateSecondArg(args, player)){
			player.sendMessage(ChatColor.RED + "supply a number must be 1-7");
			return false;
		}
		
		String rawDate = args[1];
		
	    Date parsedDate = null;
	    
	    try {
	    	parsedDate = sdf.parse(rawDate);
		} catch (ParseException e) {
			player.sendMessage(ChatColor.RED + "There was an error parsing the date you provided ["+rawDate+"].");
			plugin.debugWarning("Error parsing date ["+rawDate+"]: " + e.getMessage());
			return false;
		}
	    
	    
	    if(parsedDate == null){
	    	player.sendMessage(ChatColor.RED + "There was anunexpected error parsing the date you provided ["+rawDate+"].");
	    	plugin.debugWarning("parsedDate null");
	    	return false;
	    }
	    
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(parsedDate);
	    int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
	    
	    if(dayOfMonth > 28 || dayOfMonth < 1){
	    	player.sendMessage(ChatColor.RED + "All Yearmarked months have 28 days. " + dayOfMonth + " is not accepted");
	    	return false;
	    }
	    
	    int year = cal.get(Calendar.YEAR);
	    
	    if(year < 1){
	    	player.sendMessage(ChatColor.RED + "Yearmarked years start at 1. " + year + " is not accepted.");
	    	return false;
	    }
	    
	    int month = cal.get(Calendar.MONTH);
	    
	    if(month < 1 || month > 12){
	    	player.sendMessage(ChatColor.RED + "There are 12 Yearmarked months in each year. " + month + " is not accepted.");
	    	return false;
	    }
	    
	    int ticksInDay = 24000;
	    
	    long newFulltime = dayOfMonth*ticksInDay;
	    
	    newFulltime += (month * 28 * ticksInDay);
	    
	    newFulltime += (year * (28*12) * ticksInDay);
	    
	    setReturnValueForPlayer(player);
	    
	    player.getWorld().setFullTime(newFulltime);
	    
	    player.sendMessage("/time set " + newFulltime);
	    
		return true;
	}
	
	

	private boolean handleDayArgument(String[] args, Player player) {
		
		if(!validateSecondArg(args, player)){
			player.sendMessage(ChatColor.RED + "supply a number must be 1-7");
			return false;
		}
		
		try {
			int dayNumber = Integer.parseInt(args[1]);
			
			if(dayNumber < 1 || dayNumber > 7){
				player.sendMessage(ChatColor.RED + "number must be 1-7");
				return false;
			}else{
				setReturnValueForPlayer(player);
				int newFulltime = (dayNumber-1)*24000;
				player.sendMessage("/time set " + newFulltime);
				player.getWorld().setFullTime(newFulltime);
				return true;
			}
			
		} catch (NumberFormatException e) {
			player.sendMessage(ChatColor.RED + "error while parsing ["+args[1]+"] as an integer");
			plugin.debugInfo("issue parsing user input ["+args[1]+"] as integer: " + e.getMessage());
			return false;
		}
	}

	private boolean handleReturnArgument(String[] args, Player player) {
		Long returnValue = getReturnValueForPlayer(player);
		if(returnValue != null){
			player.getWorld().setFullTime(returnValue);
			player.sendMessage("/time set " + returnValue);
			setReturnValueForPlayer(player, null);
			return true;
		}
		player.sendMessage("no return value is presently set");
		return false;
	}
	
	private void setReturnValueForPlayer(Player player, Long value){
		if(!playerWorldReturnMap.containsKey(player.getName()) || playerWorldReturnMap.get(player.getName()) == null){
			playerWorldReturnMap.put(player.getName(), new HashMap<String,Long>());
		}
		playerWorldReturnMap.get(player.getName()).put(player.getWorld().getName(), value);
	}
	private void setReturnValueForPlayer(Player player){
		player.sendMessage(ChatColor.GREEN + "Return value set (" + player.getWorld().getFullTime() + ")");
		setReturnValueForPlayer(player, player.getWorld().getFullTime());
	}
	
	private Long getReturnValueForPlayer(Player player){
		if(!playerWorldReturnMap.containsKey(player.getName()) || playerWorldReturnMap.get(player.getName()) == null){
			playerWorldReturnMap.put(player.getName(), new HashMap<String,Long>());
		}
		return playerWorldReturnMap.get(player.getName()).get(player.getWorld().getName());
	}

}
