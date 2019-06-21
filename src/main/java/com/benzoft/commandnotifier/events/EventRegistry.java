package com.benzoft.commandnotifier.events;

import com.benzoft.commandnotifier.CommandNotifier;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.stream.Stream;

public final class EventRegistry {

    public static void registerEvents(final CommandNotifier commandNotifier, final Listener... listeners) {
        Stream.concat(Stream.of(listeners), Stream.of(
                new CommandListener(commandNotifier),
                new PlayerJoinListener(commandNotifier)
        )).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, commandNotifier));
    }
}
