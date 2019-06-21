package com.benzoft.commandnotifier.commands.commandnotifier;

import com.benzoft.commandnotifier.PluginPermission;
import com.benzoft.commandnotifier.commands.AbstractSubCommand;
import com.benzoft.commandnotifier.persistence.MessagesFile;
import com.benzoft.commandnotifier.persistence.UserdataFile;
import org.bukkit.entity.Player;

public class Toggle extends AbstractSubCommand {

    Toggle(final String commandName, final PluginPermission permission, final boolean playerOnly, final String... aliases) {
        super(commandName, permission, playerOnly, aliases);
    }

    @Override
    public void onCommand(final Player player, final String[] args) {
        UserdataFile.getInstance().getUserdata(player.getUniqueId(), true).ifPresent(userdata -> {
            userdata.toggleEnabled();
            (userdata.isEnabled() ? MessagesFile.getInstance().getEnabled() : MessagesFile.getInstance().getDisabled()).send(player);
        });
    }
}
