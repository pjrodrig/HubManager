package com.yahoo.prosfis.hubmanager;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigUtil {

	public static void saveLocation(FileConfiguration config, String path, Location loc) {
		if (loc != null) {
			config.set(path + "Set", true);
			config.set(path + "World", loc.getWorld().getName());
			config.set(path + "X", loc.getX());
			config.set(path + "Y", loc.getY());
			config.set(path + "Z", loc.getZ());
			config.set(path + "Yaw", loc.getYaw());
			config.set(path + "Pitch", loc.getPitch());
		}
	}

	public static Location loadLocation(Server server, FileConfiguration config, String path) {
		Location loc = null;
		if (config.getBoolean(path + "Set")) {
			loc = new Location(server.getWorld(config.getString(path + "World")),
					config.getDouble(path + "X"), config.getDouble(path + "Y"),
					config.getDouble(path + "Z"), Float.parseFloat(config.getString(path + "Yaw")),
					Float.parseFloat(config.getString(path + "Pitch")));

		}
		return loc;
	}
}
