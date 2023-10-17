package de.simonsator.partyandfriends.extensions.foijoj;

import de.simonsator.partyandfriends.api.PAFExtension;
import de.simonsator.partyandfriends.api.adapter.BukkitBungeeAdapter;
import de.simonsator.partyandfriends.api.pafplayers.OnlinePAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.api.pafplayers.PAFPlayerManager;
import de.simonsator.partyandfriends.extensions.foijoj.configuration.FOIOJConfig;
import de.simonsator.partyandfriends.friends.commands.Friends;
import de.simonsator.partyandfriends.friends.settings.OfflineSetting;
import de.simonsator.partyandfriends.main.Main;
import de.simonsator.partyandfriends.utilities.ConfigurationCreator;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FOIOJPlugin extends PAFExtension implements Listener {
	private ConfigurationCreator config;
	private static final Pattern FRIEND_COUNT_PATTERN = Pattern.compile("[FRIEND_COUNT]", Pattern.LITERAL);
	private static final Pattern FRIENDS_PATTERN = Pattern.compile("[FRIENDS]", Pattern.LITERAL);

	@Override
	public void onEnable() {
		try {
			config = new FOIOJConfig(new File(getConfigFolder(), "config.yml"), this);
			getAdapter().registerListener(this, this);
			registerAsExtension();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	public void onJoin(PostLoginEvent pEvent) {
		getProxy().getScheduler().schedule(this, () -> {
			OnlinePAFPlayer player = PAFPlayerManager.getInstance().getPlayer(pEvent.getPlayer());
			if (player != null && player.doesExist()) {
				List<PAFPlayer> friends = player.getFriends();
				if (friends.isEmpty())
					return;
				List<OnlinePAFPlayer> onlineFriends = new ArrayList<>(friends.size());
				for (PAFPlayer friend : friends) {
					if (friend.isOnline() && (!Main.getInstance().getGeneralConfig().getBoolean(
							"Commands.Friends.SubCommands.Settings.Settings.Offline.Enabled") ||
							friend.getSettingsWorth(OfflineSetting.SETTINGS_ID) == OfflineSetting.FRIENDS_CAN_SEE_PLAYER_IS_ONLINE_STATE)) {
						onlineFriends.add((OnlinePAFPlayer) friend);
					}
				}
				if (onlineFriends.isEmpty()) {
					player.sendMessage(Friends.getInstance().getPrefix() + config.getString("Messages.NoFriendsOnline"));
					return;
				}
				StringBuilder content = new StringBuilder();
				for (OnlinePAFPlayer onlineFriend : onlineFriends) {
					content.append(config.getString("Messages.FriendColor"));
					content.append(onlineFriend.getDisplayName());
					content.append(config.getString("Messages.PlayerSplit"));
				}
				player.sendMessage(FRIEND_COUNT_PATTERN.matcher(FRIENDS_PATTERN.matcher(Friends.getInstance().getPrefix() + config.getString("Messages.FriendsOnline")).replaceAll(Matcher.quoteReplacement(content.substring(0, content.length() - config.getString("Messages.PlayerSplit").length())))).
						replaceAll(Matcher.quoteReplacement(onlineFriends.size() + "")));
			}
		}, config.getLong("General.ScheduleMessageSeconds"), TimeUnit.SECONDS);
	}
}
