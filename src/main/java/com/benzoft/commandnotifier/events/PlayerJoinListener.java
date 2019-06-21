package com.benzoft.commandnotifier.events;

import com.benzoft.commandnotifier.CommandNotifier;
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

    public PlayerJoinListener(final CommandNotifier commandNotifier) {
        this.commandNotifier = commandNotifier;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        UserdataFile.getInstance().getUserdata(player.getUniqueId(), false).ifPresent(userdata -> commandNotifier.getLogDatabase().retrieveLogs(userdata.getLastLogout(), logContainer -> {
            if (!logContainer.getPartitionedLog().isEmpty()) {
                Bukkit.getScheduler().runTaskLater(commandNotifier, () -> {
                    MessagesFile.getInstance().getCommandsSinceLogout().send(player);
                    PlayerDataManager.getPlayerData(player, true).setLogContainer(logContainer);
                    logContainer.setPlayer(player);
                    logContainer.showEntries(0);
                }, 30);
            }
        }));
    }
}
