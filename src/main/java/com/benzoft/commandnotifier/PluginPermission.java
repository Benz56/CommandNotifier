package com.benzoft.commandnotifier;

import lombok.Getter;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public enum PluginPermission {
    COMMANDS(new Permission("commandnotifier.commands", PermissionDefault.OP)),
    COMMANDS_HELP(new Permission("commandnotifier.commands.help", PermissionDefault.OP)),
    COMMANDS_LOG(new Permission("commandnotifier.commands.log", PermissionDefault.OP)),
    COMMANDS_TOGGLE(new Permission("commandnotifier.commands.toggle", PermissionDefault.OP)),
    COMMANDS_RELOAD(new Permission("commandnotifier.commands.reload", PermissionDefault.OP)),
    NOTIFIABLE(new Permission("commandnotifier.notify", PermissionDefault.FALSE)),
    UPDATE(new Permission("commandnotifier.update", PermissionDefault.FALSE));

    @Getter
    private final Permission permission;

    PluginPermission(final Permission permission) {

        this.permission = permission;
    }

    public boolean checkPermission(final Permissible player) {
        return player == null || player.hasPermission(permission);
    }
}
