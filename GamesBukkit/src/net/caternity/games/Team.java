package net.caternity.games;

import org.bukkit.Location;

public class Team {
	static public enum GAMETYPE{
		ARCSOFT,
		FLAG,
		BATTLE
	};
	public GAMETYPE gameType;
	public boolean hardcore = false;
	public Location spawn;
	public String name;
}
