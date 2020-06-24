package com.benzoft.commandnotifier;

import com.benzoft.commandnotifier.persistence.ConfigFile;
import com.benzoft.commandnotifier.tasks.AsyncTask;
import com.benzoft.commandnotifier.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.IntStream;

class UpdateChecker implements Listener {

    private static final int ID = 68554;

    private final JavaPlugin javaPlugin;
    private final String localPluginVersion;
    private String spigotPluginVersion;

    UpdateChecker(final JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        localPluginVersion = javaPlugin.getDescription().getVersion();
    }

    void checkForUpdate() {
        new BukkitRunnable() {
            @Override
            public void run() {
                //The request is executed asynchronously as to not block the main thread.
                AsyncTask.supplyAsync(() -> {
                    if (!ConfigFile.getInstance().isUpdateCheckerEnabled()) return;
                    //Request the current version of your plugin on SpigotMC.
                    try {
                        final HttpsURLConnection connection = (HttpsURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=" + ID).openConnection();
                        connection.setRequestMethod("GET");
                        spigotPluginVersion = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                    } catch (final IOException e) {
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUpdate checker failed! Disabling."));
                        e.printStackTrace();
                        cancel();
                        return;
                    }

                    if (isLatestVersion()) return;

                    MessageUtil.send(null, "&7[&9CommandNotifier&7] &fA new update is available at:");
                    MessageUtil.send(null, "&bhttps://www.spigotmc.org/resources/" + ID + "/updates");

                    //Register the PlayerJoinEvent
                    Bukkit.getScheduler().runTask(javaPlugin, () -> Bukkit.getPluginManager().registerEvents(new Listener() {
                        @EventHandler(priority = EventPriority.MONITOR)
                        public void onPlayerJoin(final PlayerJoinEvent event) {
                            final Player player = event.getPlayer();
                            if (PluginPermission.UPDATE.hasPermission(player) || (player.isOp() && !ConfigFile.getInstance().isUpdateCheckerPermissionOnly())) {
                                MessageUtil.send(event.getPlayer(), "&7[&9CommandNotifier&7] &fA new update is available at:");
                                MessageUtil.send(event.getPlayer(), "&bhttps://www.spigotmc.org/resources/" + ID + "/updates");
                            }
                        }
                    }, javaPlugin));

                    //Cancel the runnable as an update is found.
                    cancel();
                }).complete();
            }
        }.runTaskTimer(javaPlugin, 0, 12_000);
    }

    private boolean isLatestVersion() {
        try {
            final int[] local = Arrays.stream(localPluginVersion.split("\\.")).mapToInt(Integer::parseInt).toArray();
            final int[] spigot = Arrays.stream(spigotPluginVersion.split("\\.")).mapToInt(Integer::parseInt).toArray();
            return IntStream.range(0, local.length).filter(i -> local[i] != spigot[i]).limit(1).mapToObj(i -> local[i] >= spigot[i]).findFirst().orElse(true);
        } catch (final NumberFormatException ignored) {
            return localPluginVersion.equals(spigotPluginVersion);
        }
    }
}
