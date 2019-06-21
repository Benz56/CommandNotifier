package com.benzoft.commandnotifier.commands.commandnotifier;

import com.benzoft.commandnotifier.PluginPermission;
import com.benzoft.commandnotifier.commands.AbstractSubCommand;
import com.benzoft.commandnotifier.persistence.AbstractFile;
import com.benzoft.commandnotifier.persistence.ConfigFile;
import com.benzoft.commandnotifier.persistence.MessagesFile;
import com.benzoft.commandnotifier.persistence.UserdataFile;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.stream.Stream;

public class Reload extends AbstractSubCommand {

    private final Plugin plugin;

    Reload(final Plugin plugin, final String commandName, final PluginPermission permission, final boolean playerOnly, final String... aliases) {
        super(commandName, permission, playerOnly, aliases);
        this.plugin = plugin;
    }

    @Override
    public void onCommand(final Player player, final String[] args) {
        ConfigFile.reload(plugin);
        UserdataFile.getInstance().save();
        Stream.of(UserdataFile.getInstance(), MessagesFile.getInstance()).forEach(AbstractFile::reload);
        MessagesFile.getInstance().getConfigReload().send(player);
    }
}
