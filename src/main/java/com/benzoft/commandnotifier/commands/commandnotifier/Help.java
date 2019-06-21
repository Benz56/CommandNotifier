package com.benzoft.commandnotifier.commands.commandnotifier;

import com.benzoft.commandnotifier.PluginPermission;
import com.benzoft.commandnotifier.commands.AbstractSubCommand;
import com.benzoft.commandnotifier.utils.MessageUtil;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.entity.Player;

public class Help extends AbstractSubCommand {

    Help(final String commandName, final PluginPermission permission, final boolean playerOnly, final String... aliases) {
        super(commandName, permission, playerOnly, aliases);
    }

    @Override
    public void onCommand(final Player player, final String[] args) {
        MessageUtil.send(player, "&9&m&l----------------------------------");
        MessageUtil.send(player, " &7&oBelow is a list of all Command Notifier commands:");
        MessageUtil.sendJSON(player, " &7&l● &e/cn [help]", "&eOpens this help page.\n\n&7Click to run.", "/commandnotifier help");
        MessageUtil.sendJSON(player, " &7&l● &e/cn toggle", "&eToggle command notifications.\n\n&7Click to run.", "/commandnotifier toggle");
        MessageUtil.sendJSON(player, " &7&l● &e/cn log <time...>", "&eRetrieve executed commands for the specified time.\n" +
                "&eExample: /cn log 1day 2hours 1min 1sec.\n\n&7Click to suggest.", "/commandnotifier log ", ClickEvent.Action.SUGGEST_COMMAND);
        MessageUtil.sendJSON(player, " &7&l● &e/cn log page <\"next\"/\"previous\"/page>", "&eSelect a page in your currently retrieved log.\n" +
                "&eRequires you to retrieve a log beforehand.\n\n&7Click to suggest.", "/commandnotifier log page ", ClickEvent.Action.SUGGEST_COMMAND);
        MessageUtil.sendJSON(player, " &7&l● &e/cn reload", "&eReload the configuration files.\n\n&7Click to run.", "/commandnotifier reload");
        MessageUtil.send(player, "&9&m&l----------------------------------");
    }
}
