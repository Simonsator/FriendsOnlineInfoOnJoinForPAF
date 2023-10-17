package de.simonsator.partyandfriends.extensions.foijoj.configuration;

import de.simonsator.partyandfriends.api.PAFExtension;
import de.simonsator.partyandfriends.utilities.ConfigurationCreator;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class FOIOJConfig extends ConfigurationCreator {
	public FOIOJConfig(File file, PAFExtension pPlugin) throws IOException {
		super(file, pPlugin, true);
		readFile();
		loadDefaults();
		saveFile();
		process();
	}

	private void loadDefaults() {
		set("General.ScheduleMessageSeconds", 1);
		set("Messages.NoFriendsOnline", " &7Currently no friend is online.");
		set("Messages.FriendsOnline", " &7[FRIEND_COUNT] friend(s) are currently online:LINE_BREAK &7- [FRIENDS]");
		set("Messages.FriendColor", "&a");
		set("Messages.PlayerSplit", "LINE_BREAK &7- ");
	}
}
