package net.ddns.ogl.RandomTeleport;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import net.ddns.ogl.RandomTeleport.commands.RandomTeleport;

public class Main extends JavaPlugin {
	public void onEnable() {
		PluginDescriptionFile configFile = getDescription();
		registerCommands();
		registerConfig();
		Logger logger = Logger.getLogger("Minecraft");
		logger.info("[RandomTeleporter] RandomTeleport " + "v." + configFile.getVersion() + " has been enabled" );		    
	}
	public void onDisable() {
		PluginDescriptionFile configFile = getDescription();
		Logger logger = Logger.getLogger("Minecraft");
		logger.info("[RandomTeleporter] RandomTeleport " + "v." + configFile.getVersion() + " has been disabled" );
	}
	public void registerCommands() {
		getCommand("randomteleport").setExecutor(new RandomTeleport(this));
	}
	public void registerConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

}
