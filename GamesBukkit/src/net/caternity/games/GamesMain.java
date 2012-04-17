package net.caternity.games;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class GamesMain extends JavaPlugin implements Listener
{
	static public Map<String, AddedPlayer> listedPlayers = new HashMap<String, AddedPlayer>();
	static public Map<String, Team> listedTeams = new HashMap<String, Team>();
	static public Location spectatorLoc = null;
	static public GamesMain plugin;
	Logger log;
	
	
	public void onEnable(){
		new PlayerListener(this);
		plugin = this;
		spectatorLoc = getServer().getWorlds().get(0).getSpawnLocation();
		TeamSaver.instance.load();
	}
	public void onDisable(){
		TeamSaver.instance.save();
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player player = null;
		if(sender instanceof Player)
		{
			player = (Player) sender;
		}
		if(cmd.getName().equalsIgnoreCase("addPlayer")){
			if(args.length != 2 && args.length != 3)
			{
				sender.sendMessage("Incorrect usage of this command");
				return false;
			} else if(player == null) {
				sender.sendMessage("This command can only be run by a player");
			} else {
				Player target = findPlayer(args[0]);
				if(target != null)
				{
					if(!listedTeams.containsKey(args[1]))
					{
						Team newTeam = new Team();
						Team.GAMETYPE type = Team.GAMETYPE.ARCSOFT;
						if(args.length == 3)
						{
							if(args[2].equalsIgnoreCase("battle"))
							{
								type = Team.GAMETYPE.BATTLE;
							} else if(args[2].equalsIgnoreCase("flag")) {
								type = Team.GAMETYPE.FLAG;
							} else if(!args[2].equalsIgnoreCase("arcsoft")) {
								sender.sendMessage("Can't find this game type. Setting to arcsoft");
							}
						}
						newTeam.gameType = type;
						newTeam.spawn = player.getLocation();
						newTeam.name = args[1];
						listedTeams.put(args[1], newTeam);
						sender.sendMessage("The team " + args[1] + " doesn't exist. Creating this team...");
					}
					if(!listedPlayers.containsKey(target.getName()))
					{
						AddedPlayer newPlayer = new AddedPlayer();
						newPlayer.bukkitPlayer = target;
						newPlayer.team = listedTeams.get(args[1]);
						newPlayer.name = target.getName();
						target.setDisplayName(".");
						listedPlayers.put(target.getName(), newPlayer);
						sender.sendMessage("Player is added!");
						getServer().broadcastMessage("Player " + target.getName() + " has been added to team " + args[1]);
					} else {
						listedPlayers.get(target.getName()).team = listedTeams.get(args[1]);
						sender.sendMessage("Player has changed.");
						getServer().broadcastMessage("Player " + target.getName() + " has changed to team " + args[1]);
					}
					
				} else {
					sender.sendMessage("Can't find this user.");
				}
			}
			return true;
		} else if(cmd.getName().equalsIgnoreCase("setTeamSpawn")){
			if(player == null) {
				sender.sendMessage("This command can only be run by a player");
			} else if(args.length != 1) {	
				sender.sendMessage("Incorrect usage of this command");
				return false;
			} else if(!listedTeams.containsKey(args[0])){
				sender.sendMessage("Can't find the team " + args[0]);
			} else {
				listedTeams.get(args[0]).spawn = player.getLocation();
				sender.sendMessage("Spawn setted!");
				getServer().broadcastMessage("The spawn of the team " + args[0] + " has been changed.");
				return true;
			}
		} else if(cmd.getName().equalsIgnoreCase("setTeamMode")){
			if(args.length != 2) {	
				sender.sendMessage("Incorrect usage of this command");
				return false;
			} else if(!listedTeams.containsKey(args[0])){
				sender.sendMessage("Can't find the team " + args[0]);
			} else if(args[1].equalsIgnoreCase("hardcore")){
				listedTeams.get(args[0]).hardcore = true;
				sender.sendMessage("Mode setted to hardcore");
				getServer().broadcastMessage("The team " + args[0] + " is now playing in hardcore!");
				return true;
			} else {
				listedTeams.get(args[0]).hardcore = false;
				sender.sendMessage("Mode setted to default");
				getServer().broadcastMessage("The team " + args[0] + " isn't playing in hardcore!");
				return true;
			}
		} else if(cmd.getName().equalsIgnoreCase("setTeamType")){
			if(args.length != 2) {	
				sender.sendMessage("Incorrect usage of this command");
				return false;
			} else if(!listedTeams.containsKey(args[0])){
				sender.sendMessage("Can't find the team " +args[0]);
			} else {
				Team.GAMETYPE type = Team.GAMETYPE.ARCSOFT;
				if(args[2].equalsIgnoreCase("battle"))
				{
					type = Team.GAMETYPE.BATTLE;
					sender.sendMessage("Type setted to BATTLE");
					getServer().broadcastMessage("The team " + args[0] + " is now playing in BATTLE!");
				} else if(args[2].equalsIgnoreCase("flag")) {
					type = Team.GAMETYPE.FLAG;
					sender.sendMessage("Type setted to FLAG");
					getServer().broadcastMessage("The team " + args[0] + " is now playing in FLAG!");
				} else if(!args[2].equalsIgnoreCase("arcsoft")) {
					sender.sendMessage("Can't find this game type. Setting to arcsoft");
					getServer().broadcastMessage("The team " + args[0] + " is now playing in ARCSOFT!");
				}
				listedTeams.get(args[0]).gameType = type;
				return true;
			}
		} else if(cmd.getName().equalsIgnoreCase("setSpectatorSpawn")){
			if(player == null) {
				sender.sendMessage("This command can only be run by a player");
			} else {
				spectatorLoc = player.getLocation();
				sender.sendMessage("Spawn has been setted.");
				getServer().broadcastMessage("The spawn for the spectators has been setted.");
				return true;
			}
		} else if(cmd.getName().equalsIgnoreCase("rmPlayer")) {
			if(args.length != 1) {
				sender.sendMessage("Incorrect usage of this command");
				return false;
			} else {
				Player target = findPlayer(args[0]);
				if(target == null)
				{
					sender.sendMessage("Can't find the player.");
				} else {
					if(listedPlayers.containsKey(target.getName()))
					{
						target.setDisplayName(target.getName());
						listedPlayers.remove(target.getName());
						sender.sendMessage("Player removed!");
						getServer().broadcastMessage("The player " + target.getName() + " has been removed from the game.");
					}
					else {
						sender.sendMessage("Player is not in a team.");
					}
				}
				return true;
			}
		} else if(cmd.getName().equalsIgnoreCase("rmTeam")) {
			if(args.length != 1) {
				sender.sendMessage("Incorrect usage of this command");
				return false;
			} else {
				if(listedTeams.containsKey(args[0]))
				{
					for(AddedPlayer currentPlayer : listedPlayers.values())
					{
						if(currentPlayer.team.name.equalsIgnoreCase(args[0]))
						{
							currentPlayer.bukkitPlayer.setDisplayName(currentPlayer.name);
							listedPlayers.remove(currentPlayer.team.name);
						}
					}
					listedTeams.remove(args[0]);
					sender.sendMessage("Team removed!");
					getServer().broadcastMessage("The team " + args[0] + " has been removed from the game.");
				}
				else {
					sender.sendMessage("Team is not in the game.");
				}
				return true;
			}
		} else if(cmd.getName().equalsIgnoreCase("gamesreload")) {	
			TeamSaver.instance.load();
			sender.sendMessage("Games reloaded!");
			return true;
		} else if(cmd.getName().equalsIgnoreCase("gamessave")) {	
			TeamSaver.instance.save();
			sender.sendMessage("Games saved!");
			return true;
		} else if(cmd.getName().equalsIgnoreCase("gamesreset")) {
			listedPlayers.clear();
			listedTeams.clear();
			sender.sendMessage("Games reseted!");
			return true;
		} else if(cmd.getName().equalsIgnoreCase("setlevel")) {	
			if(args.length == 2) {
				Player target = findPlayer(args[0]);
				if(target != null) {
					int level = Integer.parseInt(args[1]);
					target.setLevel(level);
					if(player == null) {
						target.sendMessage("Console has setted you at level " + level);						
					} else {
						target.sendMessage(player.getName() + " has setted you at level " + level);
					}
					sender.sendMessage(args[0] + " is now at the level " + level);
				} else {
					sender.sendMessage("Can't find this user.");
				}
				return true;
			} else {
				
				return false;
			}
		}
		return false; 
	}
	static public Player findPlayer(String name)
	{
		Player player = null;
		for(int i = 0 ; i < plugin.getServer().getOnlinePlayers().length ; i++)
		{
			if(plugin.getServer().getOnlinePlayers()[i].getName().toLowerCase().contains(name.toLowerCase()))
			{
				player = plugin.getServer().getOnlinePlayers()[i];
			}
		}
		return player;
	}
}
