package com.benzoft.commandnotifier.commands;

import com.benzoft.commandnotifier.CommandNotifier;
import com.benzoft.commandnotifier.commands.commandnotifier.CNCommand;

import java.util.Objects;
import java.util.stream.Stream;

public final class CommandRegistry {

    public static void registerCommands(final CommandNotifier commandNotifier) {
        Stream.of(new CNCommand(commandNotifier, "commandnotifier")
        ).forEach(command -> Objects.requireNonNull(commandNotifier.getCommand(command.getCommandName())).setExecutor(command));
    }
}
