package io.github.NateTheCodeWizard.nicknames;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Nicknames extends JavaPlugin implements Listener {
	private static HashMap<UUID, String> originalNames = new HashMap<>();
	private static HashMap<UUID, String> nicknames = new HashMap<>();
	public static Nicknames instance;
	public static Logger logger;
	public static boolean lanceEnabled = false;

	public static boolean setNick(Player player, String nick) {
		if (!Nicknames.originalNames.containsKey(player.getUniqueId()))
			Nicknames.originalNames.put(player.getUniqueId(), player.getName());
		String old = player.getName();
		for (String s : nicknames.values()) {
			if (s.toLowerCase().equals(nick.toLowerCase()))
				return false;
		}
		for (String s : originalNames.values()) {
			if (s.toLowerCase().equals(nick.toLowerCase()))
				return false;
		}
		Nicknames.nicknames.put(player.getUniqueId(), nick);
		player.setDisplayName(nick);
		player.setPlayerListName(old);
		return true;
	}

	public void onEnable() {
		instance = this;
		logger = getLogger();
		NicknameCommand cmd = new NicknameCommand();
		getCommand("nick").setExecutor(cmd);
		getServer().getPluginManager().registerEvents(this, this);

	}

	public void onDisable() {
		clearAllNicks();
	}

	private static void clearAllNicks() {
		for (UUID id : originalNames.keySet()) {
			removeNick(Bukkit.getPlayer(id));
		}
	}

	@EventHandler
	public static void logout(PlayerQuitEvent event) {
		onQuit(event.getPlayer());
	}

	@EventHandler
	public static void kick(PlayerKickEvent event) {
		onQuit(event.getPlayer());
	}

	private static void onQuit(Player p) {
		if (hasNick(p)) {
			removeNick(p);
		}
	}

	@EventHandler
	public static void login(PlayerJoinEvent event) {
		String name = event.getPlayer().getName();
		if (nicknames.containsValue(name)) {
			for (UUID u : nicknames.keySet()) {
				if (nicknames.get(u).equals(name)) {
					removeNick(Bukkit.getPlayer(u));
					sendMessage("Your nickname was removed since the real player logged in.", "Warn",
							Bukkit.getPlayer(u));
				}
			}
		}
		setNick(event.getPlayer(), event.getPlayer().getName());
	}

	public static String getOriginalName(Player p) {
		return originalNames.get(p.getUniqueId());
	}

	public static String getNickname(Player p) {
		return nicknames.get(p.getUniqueId());
	}

	public static void removeNick(Player p) {
		if (p == null || !hasNick(p))
			return;
		nicknames.remove(p.getUniqueId());
		p.setDisplayName(originalNames.get(p.getUniqueId()));
	}

	public static boolean hasNick(Player p) {
		return nicknames.containsKey(p.getUniqueId());
	}

	public static Player getNickedPlayer(String name) {
		if (nicknames.containsValue(name)) {
			for (UUID u : nicknames.keySet()) {
				if (nicknames.get(u).equals(name))
					return Bukkit.getPlayer(u);
			}
		} else
			return Bukkit.getPlayer(name);
		return null;
	}

	public static String formatMessage(String message, String mode) {
		String newMessage = ChatColor.DARK_AQUA + "[" + ChatColor.GOLD + "Nicknames" + ChatColor.DARK_AQUA + "] ";
		ChatColor defaultColor = null;
		switch (mode.toLowerCase()) {
		case "info":
			defaultColor = ChatColor.GREEN;
			newMessage = newMessage + defaultColor + "Info: ";
			break;
		case "severe":
			defaultColor = ChatColor.RED;
			newMessage = newMessage + defaultColor + "Severe: ";
			break;
		case "alert":
		case "warn":
			defaultColor = ChatColor.DARK_RED;
			newMessage = newMessage + defaultColor + "Warn: ";
			break;
		default:
			defaultColor = ChatColor.GREEN;
			break;
		}
		newMessage = newMessage + ChatColor.translateAlternateColorCodes('&', message);
		newMessage = newMessage.replace("%1", defaultColor + "");
		return newMessage;
	}

	public static void sendUnformattedMessage(String message, CommandSender s) {
		s.sendMessage(message);
	}

	public static void sendMessage(String message, String mode, CommandSender s) {
		s.sendMessage(formatMessage(message, mode));
	}

	public static void sendMessage(String message, CommandSender s) {
		sendMessage(message, "Info", s);
	}

	public static void sendConsoleMessage(String message, String mode) {
		logger.info(formatMessage(message, mode));
	}

	public static void sendConsoleMessage(String message) {
		sendConsoleMessage(message, "Info");
	}

	public static void sendConsoleError(String message) {
		sendConsoleMessage(message, "Severe");
	}

	public static void sendUnformattedConsoleMessage(String message) {
		logger.info(message);
	}

	public static void sendConsoleError(Throwable th) {
		String message = formatMessage("An error was thrown! The error has the following stacktrace: ", "Severe");
		message += th.getStackTrace();
		sendUnformattedConsoleMessage(message);
	}

	public static boolean isANick(String s) {
		return nicknames.containsValue(s);
	}
}
