package team.tekkitandtiger.minigame.commands;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ovh.isreborn.jesus_crie.databaseapi.mysql.MyDatabase;
import ovh.isreborn.jesus_crie.databaseapi.mysql.MySqlAPI;
import team.tekkitandtiger.minigame.GameState;
import team.tekkitandtiger.minigame.Minigame;
import team.tekkitandtiger.minigame.PlayerQueue;

public class MGCommand implements CommandExecutor {
	
	public static Minigame mg = Minigame.instance;

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("mg")) {
			if(args.length == 0) {
				if(!(sender instanceof Player)) {
					sender.sendMessage("You cannot use this command!");
				} else {
					if(args.length == 0) {
						sender.sendMessage(ChatColor.BLUE + "[Minigame] Do /mg join to join a game!");
						return true;
					} else if(args.length == 1) {
						switch(args[0]) {
						case "join":
							if(GameState.getCurrentState() == GameState.LOBBY_STATE) {
								PlayerQueue.addPlayer(sender.getName());
								Bukkit.broadcastMessage(ChatColor.BLUE + "[Minigame] Player " + sender.getName() + " has been added to the game!");
								break;
							} else {
								sender.sendMessage(ChatColor.RED + "[Minigame] You cannot join the game while state is not LOBBY_STATE");
								break;
							}
						case "leave":
							PlayerQueue.removePlayer(sender.getName());
							Bukkit.broadcastMessage(ChatColor.BLUE + "[Minigame] Player " + sender.getName() + " has left the game!");
							break;
						case "stats":
							sender.sendMessage(ChatColor.BLUE + "===== STATS =====");
							/**
							 * I haven't done database stuff before,
							 * so this probably won't work lol
							 * TODO: Database handling in Java
							 * Why can't it be PHP :P
							 */
							String dbhost = mg.getConfig().getString("db_host");
							String dbuser = mg.getConfig().getString("db_user");
						    String dbport = mg.getConfig().getString("db_port");
							String dbpass = mg.getConfig().getString("db_pass");
							String dbname = mg.getConfig().getString("db_name");
							
							MyDatabase db = MySqlAPI.getDataBase(dbhost, dbport, dbname, dbuser, dbpass);
							db.rawExecute("CREATE TABLE stats ( player VARCHAR(32) NOT NULL ,  level INT(32) NOT NULL ,  experience INT(32) NOT NULL ,    PRIMARY KEY  (player))");
							db.registerTable("stats");
							ResultSet level = db.rawRequest("SELECT level FROM stats WHERE player = '" + sender.getName() + "'");
							int totalExp = 0;
							try {
								totalExp = level.getInt("experience");
							} catch (SQLException e) {
								e.printStackTrace();
							}
							ResultSet exp = db.rawRequest("SELECT level FROM stats WHERE player = '" + sender.getName() + "'");
							sender.sendMessage(ChatColor.BLUE + "Level: " + level);
							sender.sendMessage(ChatColor.BLUE + "Exp: " + exp + "/" + totalExp);
							break;
						default:
							sender.sendMessage(ChatColor.RED + "[Minigame] Unknown Command!");
							break;
						}
					}
				}
			}
		}
		return true;
	}
}
