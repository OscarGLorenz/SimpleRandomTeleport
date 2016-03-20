package net.ddns.ogl.RandomTeleport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import net.ddns.ogl.RandomTeleport.commands.RandomTeleport;

public class Main extends JavaPlugin {
    public static Main instance;
    
	public void onEnable() {
        instance = this;
		registerCommands();
		registerConfig();
		Logger logger = Logger.getLogger("Minecraft");
		logger.info("[SimpleRandomTeleporter] " + lang.getString("CONSOLE_MESSAGE_ENABLED").split("%s")[0] + config.getString("version") + lang.getString("CONSOLE_MESSAGE_ENABLED").split("%s")[1]);		    
	}
	public void onDisable() {
		Logger logger = Logger.getLogger("Minecraft");
		logger.info("[SimpleRandomTeleporter]" + lang.getString("CONSOLE_MESSAGE_DISABLED").split("%s")[0] + config.getString("version") + lang.getString("CONSOLE_MESSAGE_DISABLED").split("%s")[1]);		    
	}
	public void registerCommands() {
		getCommand("randomteleport").setExecutor(new RandomTeleport(this));
	}
	
    public static File configf, langf;
    public FileConfiguration config;
	public FileConfiguration lang;
    
	public void registerConfig() {

        configf = new File(getDataFolder(), "config.yml");


        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            copy(getResource("config.yml"), configf);
        }

        config = new YamlConfiguration();
        
        try {
            config.load(configf);
        } catch (Exception e) {
            e.printStackTrace();
        }        
        
        switch (config.getString("Language")) {
        	case "es":
        		langf = new File(getDataFolder(), "language_es.yml");
                if (!langf.exists()) {
                	langf.getParentFile().mkdirs();
                    copy(getResource("language_es.yml"), langf);
                 }  
        		lang = new YamlConfiguration();     

        		break;
        	default:
        		langf = new File(getDataFolder(), "language_en.yml");
                if (!langf.exists()) {
                	langf.getParentFile().mkdirs();
                    copy(getResource("language_en.yml"), langf);
                 }  
        		lang = new YamlConfiguration();
        		break;
        		
        }
  
        try {
            lang.load(langf);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
	
	public void copy(InputStream in, File file) {

        try {

            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {

                out.write(buf, 0, len);

            }
            out.close();
            in.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

}
