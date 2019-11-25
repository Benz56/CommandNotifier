package com.benzoft.commandnotifier.events;

import com.benzoft.commandnotifier.CommandNotifier;
import com.benzoft.commandnotifier.PluginPermission;
import com.benzoft.commandnotifier.persistence.ConfigFile;
import com.benzoft.commandnotifier.persistence.MessagesFile;
import com.benzoft.commandnotifier.persistence.UserdataFile;
import com.benzoft.commandnotifier.runtimedata.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final CommandNotifier commandNotifier;

    PlayerJoinListener(final CommandNotifier commandNotifier) {
        this.commandNotifier = commandNotifier;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        UserdataFile.getInstance().getUserdata(player.getUniqueId(), false).ifPresent(userdata -> {
            if (userdata.isEnabled() && PluginPermission.COMMANDS_LOG.hasPermission(event.getPlayer())) {
                commandNotifier.getLogDatabase().retrieveLogs(userdata.getLastLogout()).whenComplete(logContainer -> {
                    Bukkit.getScheduler().runTaskLater(commandNotifier, () -> {
                        PlayerDataManager.getPlayerData(player, true).setLogContainer(logContainer);
                        logContainer.setPlayer(player);
                        if (ConfigFile.getInstance().isShowLogOnJoin() && !logContainer.getPartitionedLog().isEmpty()) {
                            MessagesFile.getInstance().getCommandsSinceLogout().send(player);
                            logContainer.showEntries(0);
                        }
                    }, 30);
                });
            }
        });
    }
}
