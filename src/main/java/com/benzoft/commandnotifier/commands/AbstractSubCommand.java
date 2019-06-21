package com.benzoft.commandnotifier.commands;

import com.benzoft.commandnotifier.PluginPermission;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public abstract class AbstractSubCommand extends AbstractCommand {

    private final Set<String> aliases;
    private final PluginPermission permission;
    private final boolean playerOnly;

    protected AbstractSubCommand(final String commandName, final PluginPermission permission, final boolean playerOnly, final String... aliases) {
        super(commandName);
        this.permission = permission;
        this.playerOnly = playerOnly;
        this.aliases = new HashSet<>(Arrays.asList(aliases));
    }

    public List<String> onTabComplete(final Player player, final String[] args) {
        return Collections.emptyList();
    }
}
