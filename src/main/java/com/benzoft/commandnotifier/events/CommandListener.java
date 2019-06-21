package com.benzoft.commandnotifier.events;

import com.benzoft.commandnotifier.CommandNotifier;
import com.benzoft.commandnotifier.persistence.ConfigFile;
import com.benzoft.commandnotifier.persistence.MessagesFile;
import com.benzoft.commandnotifier.persistence.UserdataFile;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class CommandListener implements Listener {

    private final Map<String, Set<String>> registeredCommands = new HashMap<>();
    private final CommandNotifier commandNotifier;

    CommandListener(final CommandNotifier commandNotifier) {
        this.commandNotifier = commandNotifier;
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(commandNotifier, () -> {
            try {
                final Field commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                commandMap.setAccessible(true);
                ((SimpleCommandMap) commandMap.get(Bukkit.getServer())).getCommands().forEach(command -> registeredCommands.computeIfAbsent(command.getName().replaceAll("/", ""), c -> new HashSet<>()).addAll(command.getAliases().stream().map(s -> s.replaceAll("/", "")).collect(Collectors.toSet())));
            } catch (final Exception e) {
                e.printStackTrace();
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onConsoleCommand(final ServerCommandEvent event) {
        if (ConfigFile.getInstance().isConsoleCommandNotifications() && event.getSender() instanceof ConsoleCommandSender)
            processCommand(null, event.getCommand());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommand(final PlayerCommandPreprocessEvent event) {
        if (ConfigFile.getInstance().isPlayerCommandNotifications()) processCommand(event.getPlayer(), event.getMessage());
    }

    private void processCommand(final Player player, final String message) {
        final String command = message.replaceAll("/", "").split(" ")[0].toLowerCase();
        if ((!ConfigFile.getInstance().isIgnoreInvalidCommands() || isCommand(command)) && !isIgnoredCommand(command)) {
            commandNotifier.getLogDatabase().logCommand(player, new ArrayList<>(getAllCommandAliases(command)).get(0), message);
            UserdataFile.getInstance().getUserdata().entrySet().stream().filter(entry -> entry.getValue().isEnabled() && (player == null || !entry.getKey().equals(player.getUniqueId()))).forEach(entry -> MessagesFile.getInstance().getExecutedCommand().replaceAll("%player%", player != null ? player.getName() : "Console").replaceAll("%command%", message.startsWith("/") ? message : "/" + message).send(Bukkit.getPlayer(entry.getKey())));
        }
    }

    private boolean isIgnoredCommand(final String command) {
        return ConfigFile.getInstance().getIgnoreList().stream().anyMatch(s -> getAllCommandAliases(command).contains(s));
    }

    /**
     * @param command The parent command or an alias of a parent command.
     * @return A set including the parent command and all of its aliases.
     */
    private Set<String> getAllCommandAliases(final String command) {
        final Set<String> aliases = new LinkedHashSet<>();
        final Optional<String> parent = registeredCommands.entrySet().stream().filter(entrySet -> entrySet.getKey().equals(command) || entrySet.getValue().contains(command)).map(Map.Entry::getKey).findFirst();
        if (parent.isPresent()) {
            aliases.add(parent.get());
            aliases.addAll(registeredCommands.get(parent.get()));
        }
        return aliases;
    }

    private boolean isCommand(final String command) {
        return registeredCommands.keySet().contains(command) || registeredCommands.values().stream().anyMatch(aliases -> aliases.contains(command));
    }
}
