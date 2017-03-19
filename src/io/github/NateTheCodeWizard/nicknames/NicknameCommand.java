package io.github.NateTheCodeWizard.nicknames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NicknameCommand implements CommandExecutor {
	public int minArgs;
	public int maxArgs;
	public boolean allowConsoleSenders;
	public String usage;

	public NicknameCommand() {
		minArgs = 1;
		maxArgs = 3;
		usage = "/nick <name, clear>";
	}

	@Override
	public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// try {
		if (!allowConsoleSenders && !(sender instanceof Player)) {
			Nicknames.sendMessage("You cannot do this.", "Warn", sender);
			return true;
		}
		if (args.length < minArgs || args.length > maxArgs) {
			Nicknames.sendMessage("Incorrect usage.", "Warn", sender);
			return true;
		}
		run(sender, command, label, args);
		// } catch (Throwable th) {
		// Nicknames.sendMessage("Error", "Severe", sender);
		// Nicknames.sendConsoleError(th);
		// }
		return true;
	}

	public void run(CommandSender sender, Command command, String label, String[] args) {
		switch (args[0]) {
		case "get":
			if (args.length < 2) {
				Nicknames.sendMessage("Use an argument.", "Warn", sender);
				return;
			}
			if (!sender.hasPermission("nicknames.get")) {
				Nicknames.sendMessage("You cannot do this.", "Warn", sender);
				return;
			}
			if (Nicknames.isANick(args[1].replace("__", " "))) {
				Nicknames.sendMessage("The user with the nickname " + ChatColor.GOLD + args[1].replace("__", " ")
						+ "%1 is known as " + ChatColor.GOLD
						+ Nicknames.getOriginalName(Nicknames.getNickedPlayer(args[1].replace("__", " "))) + "%1.",
						sender);
			} else if (Bukkit.getPlayer(args[1]) != null) {
				Player pl = Bukkit.getPlayer(args[1]);
				if (Nicknames.hasNick(pl)) {
					Nicknames.sendMessage("The user with the name " + ChatColor.GOLD + args[1]
							+ "%1 has the nickname of " + ChatColor.GOLD + Nicknames.getNickname(pl) + "%1.", sender);
				} else {
					Nicknames.sendMessage("The user " + ChatColor.GOLD + pl.getName() + "%1 does not have a nickname.",
							sender);
				}
			} else {
				Nicknames.sendMessage("Not a player!", "Warn", sender);
			}
			break;
		case "clear":
			if (args.length == 1) {
				if (!sender.hasPermission("nicknames.clear")) {
					Nicknames.sendMessage("You cannot do this.", "Warn", sender);
					return;
				}
				if (!(sender instanceof Player)) {
					Nicknames.sendMessage("You cannot do this.", "Warn", sender);
					return;
				}
				Player pl = (Player) sender;
				Nicknames.removeNick(pl);
				Nicknames.sendMessage("Nickname cleared.", pl);
			} else if (args.length == 2) {
				if (!sender.hasPermission("nicknames.clear.other")) {
					Nicknames.sendMessage("You cannot do this.", "Warn", sender);
					return;
				}
				Player pl = Bukkit.getPlayer(args[1]);
				if (pl == null) {
					Nicknames.sendMessage("Not a player!", "Warn", sender);
					return;
				}
				if (!Nicknames.hasNick(pl)) {
					Nicknames.sendMessage("That player has no nickname!", sender);
				}
				Nicknames.removeNick(pl);
				Nicknames.sendMessage(ChatColor.GOLD + Nicknames.getOriginalName(pl) + "%1 nickname was cleared.",
						sender);
				Nicknames.sendMessage("Nickname cleared.", pl);
			}

			break;
		default:
			if (args.length == 1) {
				if (!sender.hasPermission("nicknames.set")) {
					Nicknames.sendMessage("You cannot do this.", "Warn", sender);
					return;
				}
				if (!(sender instanceof Player)) {
					Nicknames.sendMessage("You cannot do this.", "Warn", sender);
					return;
				}
				Player p1 = (Player) sender;
				if (sender.hasPermission("nicknames.spaces"))
					args[0] = args[0].replace("__", " ");
				if (sender.hasPermission("nicknames.color"))
					args[0] = ChatColor.translateAlternateColorCodes('&', args[0]);
				if (Nicknames.setNick(p1, args[0]))
					Nicknames.sendMessage("Nickname set.", p1);
				else
					Nicknames.sendMessage("Name taken.", "Warn", p1);
			} else if (args.length == 2) {
				if (!sender.hasPermission("nicknames.set.other")) {
					Nicknames.sendMessage("You cannot do this.", "Warn", sender);
					return;
				}
				Player pl = Bukkit.getPlayer(args[1]);
				if (pl == null) {
					Nicknames.sendMessage("Not a player!", "Warn", sender);
					return;
				}
				if (Nicknames.setNick(pl, args[0])) {
					Nicknames.sendMessage("Nickname set.", sender);
					Nicknames.sendMessage(ChatColor.GOLD + sender.getName() + " %1has set your nickname to "
							+ ChatColor.GOLD + args[0] + "%1.", pl);
				} else {
					Nicknames.sendMessage("Name taken.", "Warn", sender);
				}
			}
			return;
		}

	}
}
