package com.benzoft.commandnotifier;

import lombok.Getter;
import org.bukkit.permissions.Permissible;

public enum PluginPermission {
    COMMANDS("commandnotifier.commands"),
    COMMANDS_HELP("commandnotifier.commands.help"),
    COMMANDS_LOG("commandnotifier.commands.log"),
    COMMANDS_TOGGLE("commandnotifier.commands.toggle"),
    COMMANDS_RELOAD("commandnotifier.commands.reload"),
    NOTIFIABLE("commandnotifier.notify"),
    UPDATE("commandnotifier.update"),
    LOG_BYPASS("commandnotifier.log.bypass");

    @Getter
    private final String permission;

    PluginPermission(final String permission) {

        this.permission = permission;
    }

    public boolean hasPermission(final Permissible player) {
        return player == null || player.hasPermission(permission);
    }
}
