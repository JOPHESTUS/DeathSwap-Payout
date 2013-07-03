package me.jophestus.plugins.deathswappayout;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import com.hawkfalcon.deathswap.API.DeathSwapWinEvent;
import com.hawkfalcon.deathswap.API.DeathSwapNewGameEvent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class DeathSwapPayout extends JavaPlugin implements Listener {
	public static Economy econ = null;
	public static Permission perms = null;
	private static final Logger log = Logger.getLogger("Minecraft");

	public void onDisable() {
		// TODO: Place any custom disable code here.
	}

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);

		if (!setupEconomy()) {
			log.severe(String.format(
					"[%s] - Disabled due to no Vault dependency found!",
					getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		SetupConfig();
	}

	private void SetupConfig() {
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	@EventHandler
	public void winGame(DeathSwapWinEvent event) 
	{
		int payloser = getConfig().getInt("loseramount");
		int paywinner = getConfig().getInt("paywinneramount");
		Player loser = Bukkit.getServer().getPlayer(event.getLoser());
		Player winner = Bukkit.getServer().getPlayer(event.getWinner());
		String payloseroption = getConfig().getString("payloser");

			
		if (payloseroption.equalsIgnoreCase("give")) {

			econ.depositPlayer(event.getLoser(), payloser);
			loser.sendMessage(ChatColor.YELLOW + "[" + ChatColor.GREEN
					+ "DeathSwap Payout" + ChatColor.YELLOW + "]"
					+ ChatColor.RED + " You've received an award of $"
					+ payloser + " for participating in the match. Well done.");

		} else if (payloseroption.equalsIgnoreCase("take")) {
			econ.withdrawPlayer(event.getLoser(), payloser);
			loser.sendMessage(ChatColor.YELLOW
					+ "["
					+ ChatColor.GREEN
					+ "DeathSwap Payout"
					+ ChatColor.YELLOW
					+ "]"
					+ ChatColor.RED
					+ " Unfortunately, because you lost the last match, the amount of $"
					+ payloser
					+ " has been taken from your account. Better luck next time.");
		}
		if (getConfig().getBoolean("paywinner")) {

			econ.depositPlayer(event.getWinner(), paywinner);

			winner.sendMessage("" + ChatColor.YELLOW + "[" + ChatColor.GREEN
					+ "DeathSwap Payout" + ChatColor.YELLOW + "]"
					+ ChatColor.RED + " You've received an award of $"
					+ paywinner + " for winning the match. Well done."); 
			}

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	@EventHandler
	public void newGame(DeathSwapNewGameEvent event) {
		if (getConfig().getBoolean("paytoplay")) {
			int cost = getConfig().getInt("costtoplay");
			econ.withdrawPlayer(event.getNameOne(), cost);
			econ.withdrawPlayer(event.getNameTwo(), cost);
			Player one = Bukkit.getServer().getPlayer(event.getNameOne());
			Player two = Bukkit.getServer().getPlayer(event.getNameTwo());
			one.sendMessage("" + ChatColor.YELLOW + "[" + ChatColor.GREEN
					+ "DeathSwap Payout" + ChatColor.YELLOW + "]"
					+ ChatColor.RED + "This game costs $" + cost + ". $" + cost
					+ " has been taken out of your account.");

			two.sendMessage("" + ChatColor.YELLOW + "[" + ChatColor.GREEN
					+ "DeathSwap Payout" + ChatColor.YELLOW + "]"
					+ ChatColor.RED + "This game costs $" + cost + ". $" + cost
					+ " has been taken out of your account.");

		}
	}

}
