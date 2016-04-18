package com.yahoo.prosfis.hubmanager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import net.md_5.bungee.api.ChatColor;

public final class ServerRegionSelectListener implements Listener {

	private final Player player;
	private final String server;
	private final HubManager hm;
	private Location selected;

	public ServerRegionSelectListener(Player player, String server, HubManager hm) {
		this.player = player;
		this.server = server;
		this.hm = hm;
		hm.getServer().getPluginManager().registerEvents(this, hm);
		player.sendMessage(ChatColor.LIGHT_PURPLE + "Click the location you wish to select.");
	}

	//detects when a player who used /CFset punches a golden pressure plate
	@EventHandler
	public void onBlockLeftClick(PlayerInteractEvent event) {
		if(event.getPlayer().equals(player)){
			Location loc = event.getClickedBlock().getLocation();
			if(selected == null){
				selected = loc;
				player.sendMessage(ChatColor.GREEN + "First location selected.");
			} else {
				hm.addServerRegion(server, selected, loc);
				player.sendMessage(ChatColor.GREEN + "A region for " + server + " has been created.");
				PlayerInteractEvent.getHandlerList().unregister(this);
			}
			event.setCancelled(true);
		}
	}
}
