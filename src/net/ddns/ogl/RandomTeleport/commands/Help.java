package net.ddns.ogl.RandomTeleport.commands;

import org.bukkit.entity.Player;

import net.ddns.ogl.RandomTeleport.Main;
import net.md_5.bungee.api.ChatColor;

public class Help {
	private static Main plugin;
	
	public static void help(Player player) {
		player.sendMessage(ChatColor.GRAY + "─────────────────────────────\n"
				+ ChatColor.BOLD + ChatColor.YELLOW + "Simple" + ChatColor.GOLD + "Random" +
				ChatColor.YELLOW + "Teleport v." + plugin.getConfig().getString("version") + "\n" + ChatColor.GOLD + "─────────────────────────────\n"
				+ ChatColor.YELLOW + "/rtp tp" + ChatColor.GOLD + 
				"Teletransporte a un punto aleatorio en este mundo\n"
				+ ChatColor.YELLOW + "/rtp help" + ChatColor.GOLD + 
				"Muestra este menú de ayuda" + ChatColor.GRAY + "─────────────────────────────\n" +
				ChatColor.GRAY + "Desarrollado por " + ChatColor.GREEN + "ospa555"  + ChatColor.GRAY + "─────────────────────────────\n"
				);
	}
	
	public static void printPlayer(String string, Player player) {
		player.sendMessage(ChatColor.DARK_AQUA + "[" + ChatColor.GOLD + "RandomTeleport"
				+ ChatColor.DARK_AQUA +"]" + string);
	}
}
