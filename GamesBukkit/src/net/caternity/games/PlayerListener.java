package net.caternity.games;

import java.util.Random;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerListener implements Listener {
	private GamesMain plugin;
	public Random rnd = new Random();
	public PlayerListener(GamesMain plug)
	{
		plugin = plug;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		if(GamesMain.listedPlayers.containsKey(event.getPlayer().getName()))
		{
			final PlayerRespawnEvent respawnEvent = event;
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
				public void run()
				{
					GamesMain.listedPlayers.get(respawnEvent.getPlayer().getName()).onPlayerRespawn(respawnEvent);
				}
			});
			
		}
	}
	@EventHandler
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent event)
	{
		final PlayerGameModeChangeEvent gmEvent = event;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			public void run()
			{
				if(GamesMain.listedPlayers.containsKey(gmEvent.getPlayer().getName())
						&& gmEvent.getPlayer().getGameMode() != GameMode.SURVIVAL)
				{
					gmEvent.getPlayer().setGameMode(GameMode.SURVIVAL);
				}
			}
		});
	}
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		final PlayerJoinEvent gmEvent = event;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			public void run()
			{
				if(GamesMain.listedPlayers.containsKey(gmEvent.getPlayer().getName()))
				{
					GamesMain.listedPlayers.get(gmEvent.getPlayer().getName()).bukkitPlayer = gmEvent.getPlayer();
				}
			}
		});
	}
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		int rndNumber = rnd.nextInt(2);
		if(event.getEntity().getName().equals("Salemminou")) {
			event.setDeathMessage("P*tain! Salemminou est le seul player qui sait bien jouer et vous le laisser crever... Bande de m*rdes...");
		} else if(event.getEntity().getName().equals("Lozerio")) {
			event.setDeathMessage("Adrien, tu fais grâve chier... T'as pas intérêt à avoir perdu du stuff!!!");
		} else if(event.getEntity().getName().equals("Kirby84")) {
			event.setDeathMessage("Hé, Kirby: TROLL TROLL TROLL!!!");
		} else if(event.getDeathMessage().contains("the ground")) {
			event.setDeathMessage("Bon, 'faudrait expliquer à " + event.getEntity().getName() + " que dans Minecraft, on ne peut voler qu'en créatif...");
		} else if(event.getDeathMessage().contains("to swim in the")) {
			event.setDeathMessage("Alors, " + event.getEntity().getName() + ", je t'explique, la lave, c'est chaud... Ca fait bobo...");
		} else if(event.getDeathMessage().contains("in a wall")) {
			event.setDeathMessage("Pff, " + event.getEntity().getName() + ", t'es vraiment un boulet...");
		} else if(rndNumber == 1){
			event.setDeathMessage("Savourez la défaite de " + event.getEntity().getName());
		} else {
			event.setDeathMessage("AH! " + event.getEntity().getName() + ", T'AS CREVE COMME UNE SALE MERDE! ALORS CA FAIT QUOI? BOUFFE, PREND CA DANS TES DENTS! OUH, TU SAVOURES HEIN? T'AIMES CA HEIN? ET BEN T'ES MORT! BON RESPAWN ET FAIT GAFFE HEIN? FAUDRAIT PAS MOURIR DEUX FOIS, CE SERAIT BETE. BONNE CHANCE JEUNE PADAWAN. ALLEZ, VAS-Y. QUELQU'UN A LU CA? SI OUI, T'AS PERDU TON TEMPS! XD BIG NOOB!");
		}
		event.setNewTotalExp(event.getEntity().getTotalExperience());
	}
}
