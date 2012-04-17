package net.caternity.games;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TeamSaver {
	FileConfiguration config = GamesMain.plugin.getConfig();
	static public TeamSaver instance = new TeamSaver();

	@SuppressWarnings("rawtypes")
	public void save(){
		GamesMain.plugin.saveConfig();
		Iterator it = GamesMain.listedPlayers.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			config.set("Players."+pairs.getKey()+".spectator", ((AddedPlayer)pairs.getValue()).isSpectator);
			config.set("Players."+pairs.getKey()+".team", ((AddedPlayer)pairs.getValue()).team.name);
			it.remove();
		}
		it = GamesMain.listedTeams.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			if(((Team)pairs.getValue()).gameType == Team.GAMETYPE.BATTLE)
			{
				config.set("Teams."+pairs.getKey()+".gametype", "BATTLE");
			} else if(((Team)pairs.getValue()).gameType == Team.GAMETYPE.FLAG){
				config.set("Teams."+pairs.getKey()+".gametype", "FLAG");
			} else {
				config.set("Teams."+pairs.getKey()+".gametype", "ARCSOFT");
			}
			config.set("Teams."+pairs.getKey()+".hardcore", ((Team)pairs.getValue()).hardcore);
			config.set("Teams."+pairs.getKey()+".xSpawn", ((Team)pairs.getValue()).spawn.getX());
			config.set("Teams."+pairs.getKey()+".ySpawn", ((Team)pairs.getValue()).spawn.getY());
			config.set("Teams."+pairs.getKey()+".zSpawn", ((Team)pairs.getValue()).spawn.getZ());
			config.set("Teams."+pairs.getKey()+".worldSpawn", ((Team)pairs.getValue()).spawn.getWorld().getName());
			it.remove();
		}
		config.set("Games.xSpectator", GamesMain.spectatorLoc.getX());
		config.set("Games.ySpectator", GamesMain.spectatorLoc.getY());
		config.set("Games.zSpectator", GamesMain.spectatorLoc.getZ());
		config.set("Games.worldSpectator", GamesMain.spectatorLoc.getWorld().getName());
		GamesMain.plugin.saveConfig();
	}
	public void load(){
		if(config.getConfigurationSection("Teams") != null) {
			Set<String> teamsList = config.getConfigurationSection("Teams").getKeys(false);
			for(String name : teamsList)
			{
				Team team = new Team();
				team.name = name;
				team.spawn = new Location(GamesMain.plugin.getServer().getWorld(config.getString("Teams."+name+".worldSpawn")),
						config.getInt("Teams."+name+".xSpawn"), config.getInt("Teams."+name+".ySpawn"),
						config.getInt("Teams."+name+".zSpawn"));
				team.hardcore = config.getBoolean("Teams."+name+".hardcore");
				if(config.getString("Teams."+name+".gametype").equals("BATTLE")){
					team.gameType = Team.GAMETYPE.BATTLE;
			} else if(config.getString("Teams."+name+".gametype").equals("FLAG")){
				team.gameType = Team.GAMETYPE.FLAG;
			} else {
				team.gameType = Team.GAMETYPE.ARCSOFT;
			}
			GamesMain.listedTeams.put(name, team);
			}
		}
		if(config.getConfigurationSection("Players") != null) {
			Set<String> playerList = config.getConfigurationSection("Players").getKeys(false);
			for(String name : playerList)
			{
				Player player = GamesMain.findPlayer(name);
				AddedPlayer addedPlayer= new AddedPlayer();
				addedPlayer.bukkitPlayer = player;
				addedPlayer.isSpectator = config.getBoolean("Players."+name+".spectator");
				addedPlayer.team = GamesMain.listedTeams.get(config.getString("Players."+name+".team"));
				GamesMain.listedPlayers.put(name, addedPlayer);
			}
			GamesMain.spectatorLoc = new Location(GamesMain.plugin.getServer().getWorld(config.getString("Games.worldSpectator")),
					config.getInt("Games.xSpectator"), config.getInt("Games.ySpectator"), config.getInt("Games.zSpectator"));
		}
	}
}
