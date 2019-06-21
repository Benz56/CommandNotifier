package com.benzoft.commandnotifier.commands.commandnotifier;

import com.benzoft.commandnotifier.PluginPermission;
import com.benzoft.commandnotifier.commands.AbstractSubCommand;
import org.bukkit.entity.Player;

public class Settings extends AbstractSubCommand {

    protected Settings(final String commandName, final PluginPermission permission, final boolean playerOnly, final String... aliases) {
        super(commandName, permission, playerOnly, aliases);
    }

    @Override
    public void onCommand(final Player player, final String[] args) {
        //TODO Implement various personal settings such as select Chat and/or Actionbar notifications.
    }
}
