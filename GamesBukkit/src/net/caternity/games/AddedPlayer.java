package net.caternity.games;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class AddedPlayer {
	public Player bukkitPlayer;
	public String name;
	public Team team;
	public boolean isSpectator;
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		if(bukkitPlayer == null)
		{
			bukkitPlayer = event.getPlayer();
		}
		bukkitPlayer.setGameMode(GameMode.SURVIVAL);
		if(team.hardcore)
		{
			isSpectator = true;
		}
		if(team.gameType == Team.GAMETYPE.ARCSOFT && !isSpectator)
		{
			bukkitPlayer.getInventory().addItem(new ItemStack(268, 1));
			bukkitPlayer.getInventory().addItem(new ItemStack(261, 1));
			bukkitPlayer.getInventory().addItem(new ItemStack(262, 1024));
		} else if(team.gameType == Team.GAMETYPE.FLAG)
		{
			bukkitPlayer.getInventory().addItem(new ItemStack(268, 1));
		}
		if(isSpectator && GamesMain.spectatorLoc != null)
		{
			bukkitPlayer.teleport(GamesMain.spectatorLoc);
		} else {
			bukkitPlayer.teleport(team.spawn);
		}
	}
}
