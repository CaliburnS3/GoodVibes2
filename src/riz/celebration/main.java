package riz.celebration;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class main extends JavaPlugin{


    public static Plugin plugin;
    int waitTime = 10;
    Random rand = new Random();
    List<String> encouragePhrases;
    HashMap<Player, Long> cooldownTimer = new HashMap<Player, Long>();

    FileConfiguration config = this.getConfig();

    @Override
    public void onEnable(){
        plugin = this;
        this.getConfig();
        encouragePhrases = new ArrayList<String>();
        getLogger().info("Celebrate has been enabled");
        encouragePhrases.clear();
        encouragePhrases = (List<String>) config.getList("path.to.list");
        this.getConfig().set("path.to.list", encouragePhrases);
        config.options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onDisable(){
        getLogger().info("Celebrate has been disabled");
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(sender instanceof Player){
            long secondsLeft;

            //Remove players from cooldown hashmap that have waited their turn
            //this is used to clean out the hashmap as it builds up over time
            for(Player player : cooldownTimer.keySet()){
                secondsLeft = ((cooldownTimer.get(player) / 1000) + waitTime) - (System.currentTimeMillis() / 1000);
                if (secondsLeft <= 0){
                    cooldownTimer.remove(player);
                }

            }

            //Cooldown checker
            if(cooldownTimer.containsKey(sender)){

                //Checks if player has a cooldown on command usage
                secondsLeft = ((cooldownTimer.get(sender) / 1000) + waitTime) - (System.currentTimeMillis() / 1000);

                if(secondsLeft > 0 && secondsLeft < 2){
                    sender.sendMessage(ChatColor.GOLD + "Please wait for " + secondsLeft + " more second!");

                }

                else if(secondsLeft > 0){
                    sender.sendMessage(ChatColor.GOLD + "Please wait for " + secondsLeft + " more seconds!");

                }

                return true;

            }


            //if no cooldown, lets get this bread

            //command #1, sends a nice message and prompts the rest of server to encourage player
            if (cmd.getName().equalsIgnoreCase("celebrate")){
                for (Player player : Bukkit.getOnlinePlayers()){
                    if(player.getName().equals(args[0])){
                        targeted(player);
                        cooldownTimer.put((Player) sender, System.currentTimeMillis());
                        return true;
                    }

                }
                    //should only reach this line if no player found with matching name
                    sender.sendMessage(ChatColor.GOLD + "Please make sure you typed the player's name correctly!");
                    return true;
            }

            //command #2, this is just like command #1, but a random target.
            if (cmd.getName().equalsIgnoreCase("rcelebrate")) {
                int temp = rand.nextInt(Bukkit.getOnlinePlayers().size());
                int count = 0;
                if (Bukkit.getOnlinePlayers().size() < 1) {
                    return true;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (count == temp) {
                        targeted(player);
                        cooldownTimer.put((Player) sender, System.currentTimeMillis());
                        return true;
                    }
                    count++;
                }
                return true;
            }

            //command #3, sends a nice message to target
            if (cmd.getName().equalsIgnoreCase("encourage")){
                for (Player player : Bukkit.getOnlinePlayers()){
                    if (player.getName().equals(args[0])){
                        //gets a random nice phrases, and reconstructs it
                        String temp = encouragePhrases.get(rand.nextInt(encouragePhrases.size()));
                        String result = temp.replace("FILLER", sender.getName().toString());
                        player.sendMessage(ChatColor.GOLD + result);
                        sender.sendMessage(ChatColor.GOLD + "To " + player.getName() + ": " + "result");
                        cooldownTimer.put((Player) sender, System.currentTimeMillis());
                    }
                }

                //should only reach this line if no player found with matching name
                sender.sendMessage(ChatColor.GOLD + "Please make sure you typed the player's name correctly!");
                return true;
            }
        }
        return true;
    }

    public void targeted(Player target){
        target.sendMessage(ChatColor.GOLD + "We appreciate you! We hope you know that!");
        for(Player player : Bukkit.getServer().getOnlinePlayers()){
            if(!player.equals(target)){
                player.sendMessage(ChatColor.GOLD + "To show your appreciation towards " + target.getName()
                        + " do /encourage " + target.getName());
            }
        }

    }
}
