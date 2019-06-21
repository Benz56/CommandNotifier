package com.benzoft.commandnotifier.commands.commandnotifier;

import com.benzoft.commandnotifier.CommandNotifier;
import com.benzoft.commandnotifier.PluginPermission;
import com.benzoft.commandnotifier.commands.AbstractParentCommand;
import com.benzoft.commandnotifier.persistence.MessagesFile;
import org.bukkit.entity.Player;

public class CNCommand extends AbstractParentCommand {

    public CNCommand(final CommandNotifier commandNotifier, final String commandName) {
        super(commandNotifier, commandName,
                new Help("help", PluginPermission.COMMANDS_HELP, false, "h"),
                new Log(commandNotifier, "log", PluginPermission.COMMANDS_LOG, false, "l"),
                new Toggle("toggle", PluginPermission.COMMANDS_TOGGLE, true, "tog", "t"),
                new Reload(commandNotifier, "reload", PluginPermission.COMMANDS_RELOAD, false, "rel", "r")
        );
    }

    @Override
    public void onCommand(final Player player, final String[] args) {
        if (PluginPermission.COMMANDS_HELP.checkPermission(player)) {
            getSubCommands().stream().filter(subCommand -> subCommand.getCommandName().equalsIgnoreCase("help")).findFirst().ifPresent(subCommand -> subCommand.onCommand(player, args));
        } else MessagesFile.getInstance().getInvalidPermission().send(player);
    }
}
