package com.benzoft.commandnotifier.commands;

import lombok.Getter;
import org.bukkit.entity.Player;

public abstract class AbstractCommand {

    @Getter
    private final String commandName;

    AbstractCommand(final String commandName) {
        this.commandName = commandName;
    }

    public abstract void onCommand(final Player player, final String[] args);
}
