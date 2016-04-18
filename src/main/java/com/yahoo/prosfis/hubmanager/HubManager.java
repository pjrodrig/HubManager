package com.yahoo.prosfis.hubmanager;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;

import com.google.common.collect.Maps;

public class HubManager extends JavaPlugin implements Listener {

	private HashMap<String, ServerRegion> serverRegions;

	public void onEnable() {
		getLogger().info("HubManager is enabled.");
		init();
	}

	public void onDisable() {
		getLogger().info("HubManager is disabled.");
	}

	private void init() {
		serverRegions = Maps.newHashMap();
		getServer().getPluginManager().registerEvents(this, this);
		FileConfiguration config = getConfig();
		ConfigurationSection servers = config.getConfigurationSection("servers");
		if (servers != null) {
			Server hub = getServer();
			for (String server : servers.getKeys(false)) {
				Location start = ConfigUtil.loadLocation(hub, config,
						"servers." + server + ".start"),
						end = ConfigUtil.loadLocation(hub, config, "servers." + server + ".end");
				if (start != null && end != null) {
					serverRegions.put(server, new ServerRegion(server, start, end));
				}
			}
		}
	}

	@EventHandler
	public void join(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		for (Player current : getServer().getOnlinePlayers()) {
			current.hidePlayer(player);
			player.hidePlayer(current);
		}
	}

	@EventHandler
	public void click(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		String server = getTargetServer(player);
		if (server != null) {
			Server hub = getServer();
			hub.dispatchCommand(hub.getConsoleSender(), "sudo " + player.getName() + " server " +  server);
		}
	}

	private String getTargetServer(Player player) {
		BlockIterator iter = new BlockIterator(player, 100);
		Block block;
		String server = null;
		while (iter.hasNext() && server == null) {
			block = iter.next();
			if (block.getType() != Material.AIR) {
				server = getServerClicked(block.getLocation());
			}
		}
		return server;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("hmset")) {
			if (sender instanceof Player) {
				if (args.length == 1) {
					new ServerRegionSelectListener((Player) sender, args[0], this);
				} else {
					sender.sendMessage(ChatColor.RED + "/hmset <server>");
				}
			} else {
				sender.sendMessage("Only players may issue this command.");
			}
		}
		return true;
	}

	public void addServerRegion(String server, Location start, Location end) {
		serverRegions.put(server, new ServerRegion(server, start, end));
		FileConfiguration config = getConfig();
		String path = "servers." + server + ".";
		ConfigUtil.saveLocation(config, path + "start", start);
		ConfigUtil.saveLocation(config, path + "end", end);
		saveConfig();
	}

	private String getServerClicked(Location loc) {
		String server = null;
		for (ServerRegion sr : serverRegions.values()) {
			if (sr.contains(loc)) {
				server = sr.getServer();
				break;
			}
		}
		return server;
	}

	private class ServerRegion {

		private final int xMin, xMax, yMin, yMax, zMin, zMax;
		private final String server;

		public ServerRegion(String server, Location a, Location b) {
			this.server = server;
			int x1 = a.getBlockX(), x2 = b.getBlockX(), y1 = a.getBlockY(), y2 = b.getBlockY(),
					z1 = a.getBlockZ(), z2 = b.getBlockZ();
			if (x1 > x2) {
				xMax = x1;
				xMin = x2;
			} else {
				xMax = x2;
				xMin = x1;
			}
			if (y1 > y2) {
				yMax = y1;
				yMin = y2;
			} else {
				yMax = y2;
				yMin = y1;
			}
			if (z1 > z2) {
				zMax = z1;
				zMin = z2;
			} else {
				zMax = z2;
				zMin = z1;
			}
			
		}

		public boolean contains(Location loc) {
			int x = loc.getBlockX(), y = loc.getBlockY(), z = loc.getBlockZ();
			return x >= xMin && x <= xMax && y >= yMin && y <= yMax && z >= zMin && z <= zMax;
		}

		public String getServer() {
			return server;
		}
	}
}
