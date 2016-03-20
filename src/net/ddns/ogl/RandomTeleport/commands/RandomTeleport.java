package net.ddns.ogl.RandomTeleport.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.ddns.ogl.RandomTeleport.Main;
import net.md_5.bungee.api.ChatColor;

public class RandomTeleport implements CommandExecutor {
	public RandomTeleport(Main pl) {
	}

// COOLDOWNS
	public class cooldown{
		   public String name;
		   public long time;
		   void setName(String iname) {
			   this.name = iname;
		   }
		   void setTime(long itime) {
			   this.time = itime;
		   }		 
		   String getName() {
			   return name;
		   }
		   long getTime() {
			   return time;
		   }
	}
	
	
	ArrayList<cooldown> cooldowns = new ArrayList<cooldown>();
	
	public void insertCooldown(String iname) {
		java.util.Date date = new java.util.Date();
		long time = date.getTime();
		cooldown cool = new cooldown();
		cool.setName(iname);
		cool.setTime(time);
		cooldowns.add(cool);
	}
	
	public boolean existPlayer(String iname) {
		Iterator<cooldown> itrcooldowns = cooldowns.iterator();
		while(itrcooldowns.hasNext()) {
			cooldown cool = itrcooldowns.next();
			if (cool.getName() == iname) return true;
			}
		return false;
	}
	
	public long getTime(String iname) {
		Iterator<cooldown> itrcooldowns = cooldowns.iterator();
		java.util.Date date = new java.util.Date();
		long time = date.getTime();
		while(itrcooldowns.hasNext()) {
			cooldown cool = itrcooldowns.next();
			if (cool.getName() == iname) {
				return (time - cool.getTime());
			}
		}
		return 0;
	}
	
	public void updateCooldown(String iname) {
		java.util.Date date = new java.util.Date();
		long time = date.getTime();
	   ListIterator<cooldown> litr = cooldowns.listIterator();
		while(litr.hasNext()) {
			cooldown cool = litr.next();
			if (cool.getName() == iname) {
				cool.setName(iname);
				cool.setTime(time);
				litr.set(cool);
			}
		}
	}

	public boolean cooldown(Player player) {
		if (player.hasPermission("randomteleport.nodelay")) return true;
		String playerName = player.getName();
		if (!(existPlayer(playerName))) {	
			insertCooldown(playerName);
			return true;
		}
		if (getTime(playerName) < Main.instance.config.getInt("Cooldown")) {
			return false;
		}
		updateCooldown(playerName);
		return true;
	}

// MAIN COMMAND	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("[SimpleRandomTeleporter]" + Main.instance.lang.getString("NO_PLAYER"));
			return false;
		}

		Player player = (Player) sender;
		
		if (args.length == 1) {
			if (args[0].toString().equalsIgnoreCase("tp")) randomTP(player);
			else if (args[0].toString().equalsIgnoreCase("reload")) reloadCommand(player);
			else helpCommand(player);
		} else helpCommand(player);
		
		return true;
	} 

	
// TELEPORT	
	public void randomTP(Player player){
		if (player.hasPermission("randomteleport.tp")) {
			if (Main.instance.config.contains("Enabled-Worlds." + player.getWorld().getEnvironment().toString() + "." + player.getWorld().getName())){
				if (cooldown(player)) {
					switch (player.getWorld().getEnvironment().toString()) {
						case "NORMAL":
							rtpNormal(player, "Enabled-Worlds.NORMAL." + player.getWorld().getName());
							break;
						case "NETHER":
							rtpNether(player, "Enabled-Worlds.NETHER." + player.getWorld().getName());
							break;
						case "THE_END":
							rtpNormal(player, "Enabled-Worlds.THE_END." + player.getWorld().getName());
							break;
					}
				} else {
					long timeLeft = ((Main.instance.config.getInt("Cooldown") - getTime(player.getName()))/1000);
					printPlayer(Main.instance.lang.getString("COOLDOWN").split("%s")[0] + timeLeft + Main.instance.lang.getString("COOLDOWN").split("%s")[1]
						, player);
				}
			} else {
				printPlayer(Main.instance.lang.getString("WORLD_DISABLED"), player);
			}
		} else {
			printPlayer(Main.instance.lang.getString("NO_PERM"), player);
		}
	}
	
	public void rtpNether(Player player, String dir) {
		int x = 0;
		int z = 0;
		Location destiny = new Location(player.getWorld(), x, 100, z);
		Location idestiny = new Location(player.getWorld(), x, 100, z);
		Block block = destiny.getWorld().getBlockAt(x, 100, z);
		Block iblock = block;
		int xmin = Main.instance.config.getInt(dir + ".Coordinates.min-x");
		int xmax = Main.instance.config.getInt(dir + ".Coordinates.max-x");
		int zmin = Main.instance.config.getInt(dir + ".Coordinates.min-z");
		int zmax = Main.instance.config.getInt(dir + ".Coordinates.max-z");
		int xcenter = Main.instance.config.getInt(dir + ".Coordinates.center-x");
		int zcenter = Main.instance.config.getInt(dir + ".Coordinates.center-z");
		
		do {
			x = randomCoord(xmin, xmax , xcenter);
			z = randomCoord(zmin, zmax , zcenter);
		
			block = destiny.getWorld().getBlockAt(x, 100, z);
			int i = 0;
			for (iblock = block; (iblock.getType() != Material.AIR && iblock.getY() > 0) ; i++)  {
				iblock = block.getRelative(0, -i, 0);
			}
			
			if (iblock.getY() > 0) {
				for (iblock = block; (iblock.getType() == Material.AIR && iblock.getY() > 0) ; i--)  {
					iblock = block.getRelative(0, i, 0);
				}
			}
			
			

		} while (dangerBlocks(iblock.getType(), dir) || dangerArea(iblock, dir) || dangerCliff(iblock, dir) || airExist(iblock));
		
		printPlayer(Main.instance.lang.getString("TP_RANDOM").split("%s")[0] + x + Main.instance.lang.getString("TP_RANDOM").split("%s")[1] + z ,player);
		
		idestiny = new Location(player.getWorld(), x + 0.5, (iblock.getY() + 1), z + 0.5);
		
		player.teleport(idestiny);
		
	}
	
	public void rtpNormal(Player player, String dir) {
		int x = 0;
		int z = 0;
		Location destiny = new Location(player.getWorld(), x, 255, z);
		Location idestiny = new Location(player.getWorld(), x, 255, z);
		Block block = destiny.getWorld().getBlockAt(x, 255, z);
		Block iblock = block;
		int xmin = Main.instance.config.getInt(dir + ".Coordinates.min-x");
		int xmax = Main.instance.config.getInt(dir + ".Coordinates.max-x");
		int zmin = Main.instance.config.getInt(dir + ".Coordinates.min-z");
		int zmax = Main.instance.config.getInt(dir + ".Coordinates.max-z");
		int xcenter = Main.instance.config.getInt(dir + ".Coordinates.center-x");
		int zcenter = Main.instance.config.getInt(dir + ".Coordinates.center-z");
	
		do {
			x = randomCoord(xmin, xmax , xcenter);
			z = randomCoord(zmin, zmax , zcenter);
		
			block = destiny.getWorld().getBlockAt(x, 255, z);
			iblock = block;
			for (int i = 0; (iblock.getType() == Material.AIR && iblock.getY() > 0) ; i--)  {
				iblock = block.getRelative(0, i, 0);
			} 

		} while (dangerBlocks(iblock.getType(), dir) || dangerArea(iblock, dir) || dangerCliff(iblock, dir));
	
		printPlayer(Main.instance.lang.getString("TP_RANDOM").split("%s")[0] + x + Main.instance.lang.getString("TP_RANDOM").split("%s")[1] + z ,player);

	
		idestiny = new Location(player.getWorld(), x + 0.5, (iblock.getY() + 1), z + 0.5);
	
		player.teleport(idestiny);
	}	
	
	
	public int randomCoord(int minimum, int maximum, int center) {
		int randomCoord = 0;
		int randCoord = maximum;
		do {
			 randomCoord = new Double(Math.random() * 2 * maximum).intValue();
			 randomCoord = randomCoord - randCoord + center;
		} while (Math.abs(randomCoord) < (minimum + center));
		return randomCoord;
		
	}
	
	public boolean dangerBlocks(Material block, String dir) {
		
	    List<String> materials = Main.instance.config.getStringList(dir + ".Dangerous-Blocks");
	    Material material = null;
	    
	    for (int i = 0; i < materials.size(); i++) {
	    	material = Material.valueOf(materials.get(i));
	    	if (material.equals(block)) return true;
	    }
	    
	    return false;
	    
	}
	
	public boolean dangerArea(Block iblock, String dir) {
		int Area = Main.instance.config.getInt(dir + ".Danger-Area");	
		for (int i = -Area; i <= Area ; i++){
			for (int j = -Area; j <= Area ; j++) {
				for (int k = -Area ; k <= Area ; k++) {
					if (dangerBlocks(iblock.getRelative(i, j, k).getType(), dir) == true) return true;
				}
			}
		}
		return false;
	}
	
	public boolean dangerCliff(Block iblock, String dir) {
		int Area = Main.instance.config.getInt(dir + ".No-Cliff-Area");
		for (int i = -Area; i <= Area ; i++){
			for (int k = -Area; k <= Area ; k++) {
					if (iblock.getRelative(i, 0, k).getType() == Material.AIR) return true;
			}
		}
		return false;
	}
	
	public boolean airExist(Block iblock) {
		if(iblock.getRelative(0, 1, 0).getType() != Material.AIR || iblock.getRelative(0, 2, 0).getType() != Material.AIR) {
			return true;
		}	
		return false;
	}

	// OTHERS
	public void printPlayer(String string, Player player) {
		player.sendMessage(ChatColor.DARK_AQUA + "[" + ChatColor.GOLD + "SimpleRandomTeleport"
				+ ChatColor.DARK_AQUA +"] " + ChatColor.translateAlternateColorCodes('&', string));
	}
	
	public void reloadCommand(Player player) {
		if (player.hasPermission("randomteleport.reload")) {
			Main.instance.registerConfig();
			printPlayer(Main.instance.lang.getString("CONFIG_RELOAD"), player);
		} else {
			printPlayer(Main.instance.lang.getString("NO_PERM"), player);
		}
	}
	
	public void helpCommand(Player player) {
		player.sendMessage(ChatColor.GRAY + "\n\n-----------------------------------------------------\n"
			    + ChatColor.YELLOW + ChatColor.BOLD + "Simple" + ChatColor.GOLD + ChatColor.BOLD + "Random" +
				ChatColor.YELLOW + ChatColor.BOLD +"Teleport" + ChatColor.RED + ChatColor.BOLD + " v." + Main.instance.config.getString("version") + "\n" + ChatColor.GRAY + "-----------------------------------------------------\n"
				+ ChatColor.YELLOW + "/rtp tp " + ChatColor.GOLD + 
				Main.instance.lang.getString("HELP_TP") + "\n"
				+ ChatColor.YELLOW + "/rtp help " + ChatColor.GOLD + 
				Main.instance.lang.getString("HELP_HELP") + "\n" + ChatColor.GRAY
				+ ChatColor.YELLOW + "/rtp reload " + ChatColor.GOLD + Main.instance.lang.getString("HELP_RELOAD") +
				"\n" + ChatColor.GRAY + 
				"-----------------------------------------------------\n" + ChatColor.GOLD +
				Main.instance.lang.getString("HELP_DEV") + ChatColor.GREEN + ChatColor.BOLD + " ospa555\n" + ChatColor.GRAY +
				"-----------------------------------------------------\n" 
				);
	}
}
