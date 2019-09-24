package com.benzoft.commandnotifier.commands;

import com.benzoft.commandnotifier.PluginPermission;
import com.benzoft.commandnotifier.persistence.MessagesFile;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter(AccessLevel.PROTECTED)
public abstract class AbstractParentCommand extends AbstractCommand implements TabExecutor {

    @Getter(AccessLevel.NONE)
    private final Plugin plugin;
    private final Set<AbstractSubCommand> subCommands;

    protected AbstractParentCommand(final Plugin plugin, final String commandName, final AbstractSubCommand... subCommands) {
        super(commandName);
        this.plugin = plugin;
        this.subCommands = new HashSet<>(Arrays.asList(subCommands));
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, final Command command, final String label, final String[] args) {
        final Player player = commandSender instanceof Player ? (Player) commandSender : null;

        if (player != null && !PluginPermission.COMMANDS.hasPermission(player) && !player.isOp()) {
            MessagesFile.getInstance().getNoCommands().send(player);
            return true;
        }

        if (args.length != 0) {
            for (final AbstractSubCommand subCommand : subCommands) {
                if (!subCommand.getCommandName().equalsIgnoreCase(args[0]) && subCommand.getAliases().stream().noneMatch(alias -> alias.equalsIgnoreCase(args[0])))
                    continue;
                if (!subCommand.getPermission().hasPermission(player)) {
                    MessagesFile.getInstance().getInvalidPermission().send(player);
                    return true;
                }
                if (player == null && subCommand.isPlayerOnly()) {
                    MessagesFile.getInstance().getPlayerOnly().send(null);
                } else subCommand.onCommand(player, args);
                return true;
            }
        }

        onCommand(player, args);
        return true;
    }

    @Override
    public java.util.List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length > 1) {
            return subCommands.stream().filter(abstractSubCommand -> abstractSubCommand.getPermission().hasPermission(sender) && (abstractSubCommand.getCommandName().equalsIgnoreCase(args[0]) || abstractSubCommand.getAliases().stream().anyMatch(a -> a.equalsIgnoreCase(args[0])))).findFirst().map(abstractSubCommand -> abstractSubCommand.onTabComplete(sender instanceof Player ? (Player) sender : null, Arrays.copyOfRange(args, 1, args.length))).orElse(null);
        } else
            return PluginPermission.COMMANDS.hasPermission(sender) ? subCommands.stream().filter(abstractSubCommand -> abstractSubCommand.getPermission().hasPermission(sender)).map(AbstractSubCommand::getCommandName).filter(name -> name.startsWith(args[0].toLowerCase())).collect(Collectors.toList()) : null;
    }
}
