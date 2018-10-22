package com.chaoticcade.lastpos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin implements Listener{
	
    @Override
    public void onEnable() {
       System.out.println("LastPos Online!");
       getServer().getPluginManager().registerEvents(this, this);
       getDataFolder().mkdir(); //TODO Update so it checks and doesn't blindly create folder
    }

    @Override
    public void onDisable() {
       System.out.println("LastPos Disabled!");
       /*
        * TODO Update to save files on shutdown rather then logout.
        */
     
       
    }
	    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("lastpos") && args.length != 1){
        	sender.sendMessage("Invalid number of arguements!");
        	return true;
        }
        
        if (cmd.getName().equalsIgnoreCase("lastpos")) {
        	String whoseLastPosition = args[0];
        	/*
        	 * TODO Parse existing csv db for player name and return UUID?
        	 */
        	
	    	Map<String, String> playerData = new HashMap<String, String>();
        	File dir = getDataFolder();
        	File[] directoryListing = dir.listFiles();
        	  if (directoryListing != null) {
        	    for (File playerFile : directoryListing) {
                	Properties loadData = new Properties();
                	try {
        				loadData.load(new FileInputStream(playerFile));
        			} catch (FileNotFoundException e) {
        				// TODO Auto-generated catch block
        				System.out.println("Lastpos command file not found error!");
        				e.printStackTrace();
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				System.out.println("Lastpos command IO error!");
        				e.printStackTrace();
        			}
                	for (String key : loadData.stringPropertyNames()) {
                			playerData.put(key, loadData.get(key).toString());
                		};
        	    	if (playerData.get("Name").equalsIgnoreCase(whoseLastPosition)){
        	    		Location loc = new Location(sender.getServer().getWorld("World"), 8, 64, 8);
        	    		String lpWorld = new String(playerData.get("World"));
        	    		loc.setWorld(Bukkit.getServer().getWorld(lpWorld));
        	    		loc.setX(Double.parseDouble(playerData.get("X")));
        	    		loc.setY(Double.parseDouble(playerData.get("Y")));
        	    		loc.setZ(Double.parseDouble(playerData.get("Z")));
        	    		loc.setYaw(Float.parseFloat(playerData.get("Yaw")));
        	    		loc.setPitch(Float.parseFloat(playerData.get("Pitch")));
        	    		sender.sendMessage(ChatColor.GREEN + "Postion located! Teleporting!!");
        	    		((Player) sender).teleport(loc);
        	    		sender.getServer().getWorld("World").playSound(loc, Sound.ENTITY_ENDERMEN_TELEPORT, 15, 1);
        	    		return true;
        	    	}
        	    		else {
        	    			
        	    		}
        	 
        	    	
        	    }
        	    sender.sendMessage(ChatColor.RED + "Player location not found.");
        	  } else {
        	    // Handle the case where dir is not really a directory.
        	    // Checking dir.isDirectory() above would not be sufficient
        	    // to avoid race conditions with another process that deletes
        	    // directories.
        	  }
        	  return true;


        }
        
        //New Commands here
        
        else {
        	return false;
    	}
    }
    	
    
    
    
    @EventHandler
    public void joinEvent(PlayerJoinEvent event) throws IOException { //Create data file if needed
        String playerFile =  getDataFolder() + "\\" + (event.getPlayer().getUniqueId().toString() + ".dat");
        File file = new File(playerFile);
        if (file.createNewFile()) {
            System.out.println("Last Position file has been created for " + event.getPlayer().getDisplayName() + ".");
        } else {
       
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) { //Store logout location
        String playerFile =  getDataFolder() + "\\" + (event.getPlayer().getUniqueId().toString() + ".dat");
    	Map<String, String> playerData = new HashMap<String, String>();
    	playerData.put("Name", event.getPlayer().getDisplayName().toString());
    	playerData.put("World", ((Player) event.getPlayer()).getLocation().getWorld().getName());
    	playerData.put("X", Double.toString(((Player) event.getPlayer()).getLocation().getX()));
    	playerData.put("Y", Double.toString(((Player) event.getPlayer()).getLocation().getY()));
    	playerData.put("Z", Double.toString(((Player) event.getPlayer()).getLocation().getZ())); 
    	playerData.put("Yaw", Double.toString(((Player) event.getPlayer()).getLocation().getYaw()));
    	playerData.put("Pitch", Double.toString(((Player) event.getPlayer()).getLocation().getPitch()));
    	Properties saveProperties = new Properties();

    	for (Map.Entry<String,String> entry : playerData.entrySet()) {
    	    saveProperties.put(entry.getKey(), entry.getValue());
    	}

    	try {
			saveProperties.store(new FileOutputStream(playerFile), null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
    }

}