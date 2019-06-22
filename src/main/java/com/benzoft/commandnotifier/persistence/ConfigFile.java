package com.benzoft.commandnotifier.persistence;

import com.benzoft.commandnotifier.CommandNotifier;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;


@Getter
public final class ConfigFile {

    @Getter
    private static ConfigFile instance = new ConfigFile();

    private final boolean notifyOpsByDefault;
    private final boolean ignoreInvalidCommands;
    private final boolean playerCommandNotifications;
    private final boolean consoleCommandNotifications;
    private final boolean showLogOnJoin;
    private final boolean invertIgnoreList;
    private final List<String> ignoreList;
    private final boolean updateCheckerEnabled;
    private final boolean updateCheckerPermissionOnly;

    private ConfigFile() {
        final CommandNotifier plugin = CommandNotifier.getPlugin(CommandNotifier.class);
        plugin.saveDefaultConfig();
        final FileConfiguration config = plugin.getConfig();
        notifyOpsByDefault = config.getBoolean("NotifyOpsByDefault", false);
        ignoreInvalidCommands = config.getBoolean("IgnoreInvalidCommands", true);
        playerCommandNotifications = config.getBoolean("PlayerCommandNotifications", true);
        consoleCommandNotifications = config.getBoolean("ConsoleCommandNotifications", true);
        showLogOnJoin = config.getBoolean("ShowLogOnJoin", true);
        invertIgnoreList = config.getBoolean("InvertIgnoreList", false);
        ignoreList = config.getStringList("IgnoreList").stream().map(String::toLowerCase).collect(Collectors.toList());
        updateCheckerEnabled = config.getBoolean("UpdateCheckerEnabled", true);
        updateCheckerPermissionOnly = config.getBoolean("UpdateCheckerPermissionOnly", false);
    }

    public static void reload(final Plugin plugin) {
        plugin.reloadConfig();
        plugin.saveDefaultConfig();
        instance = new ConfigFile();
    }
}
