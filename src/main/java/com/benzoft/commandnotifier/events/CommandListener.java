package com.benzoft.commandnotifier.events;

import com.benzoft.commandnotifier.CommandNotifier;
import com.benzoft.commandnotifier.PluginPermission;
import com.benzoft.commandnotifier.persistence.ConfigFile;
import com.benzoft.commandnotifier.persistence.MessagesFile;
import com.benzoft.commandnotifier.persistence.UserdataFile;
import com.benzoft.commandnotifier.persistence.persistenceobjects.Message;
import com.benzoft.commandnotifier.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        final Player player = event.getPlayer();
        if (ConfigFile.getInstance().isPlayerCommandNotifications() && !PluginPermission.LOG_BYPASS.hasPermission(player)) {
            processCommand(player, event.getMessage());
        }
    }

    private void processCommand(final Player player, String message) {
        final String command = message.replaceAll("/", "").split(" ")[0].toLowerCase();
        if ((!ConfigFile.getInstance().isIgnoreInvalidCommands() || isCommand(command)) && !isIgnoredCommand(command)) {
            commandNotifier.getLogDatabase().logCommand(player, isCommand(command) ? new ArrayList<>(getAllCommandAliases(command)).get(0) : command, message);
            message = message.startsWith("/") ? message : "/" + message;
            final Message formatted = MessagesFile.getInstance().getExecutedCommand().replaceAll("%player%", player != null ? player.getName() : "Console").replaceAll("%command%", message);
            UserdataFile.getInstance().getUserdata().entrySet().stream().filter(entry -> entry.getValue().isEnabled() && (player == null || !entry.getKey().equals(player.getUniqueId()))).forEach(entry -> formatted.send(Bukkit.getPlayer(entry.getKey())));
            commandNotifier.getDiscordHook().sendCommand(ChatColor.stripColor(MessagesFile.getInstance().getExecutedCommandDiscord().replaceAll("%player%", player != null ? player.getName() : "Console").replaceAll("%command%", message).replaceAll("%prefix%", MessageUtil.translate(MessagesFile.getInstance().getPrefix())).toString()));
        }
    }

    private boolean isIgnoredCommand(final String command) {
        return ConfigFile.getInstance().isInvertIgnoreList() ? ConfigFile.getInstance().getIgnoreList().stream().noneMatch(s -> getAllCommandAliases(command).contains(s)) : ConfigFile.getInstance().getIgnoreList().stream().anyMatch(s -> getAllCommandAliases(command).contains(s));
    }

    /**
     * @param command The parent command or an alias of a parent command.
     * @return A set including the parent command and all of its aliases.
     */
    private Set<String> getAllCommandAliases(final String command) {
        final Set<String> aliases = new LinkedHashSet<>();
        registeredCommands.entrySet().stream().filter(entrySet -> entrySet.getKey().equals(command) || entrySet.getValue().contains(command)).map(Map.Entry::getKey).findFirst().ifPresent(parent -> {
            aliases.add(parent);
            aliases.addAll(registeredCommands.get(parent));
        });
        return aliases;
    }

    private boolean isCommand(final String command) {
        return registeredCommands.containsKey(command) || registeredCommands.values().stream().anyMatch(aliases -> aliases.contains(command));
    }
}
