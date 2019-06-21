package com.benzoft.commandnotifier.persistence;

import com.benzoft.commandnotifier.persistence.persistenceobjects.Message;
import lombok.Getter;

@Getter
public final class MessagesFile extends AbstractFile {

    private static final MessagesFile INSTANCE = new MessagesFile();

    //General
    private String prefix;
    private Message invalidPermission;
    private Message noCommands;
    private Message playerOnly;
    private Message invalidArguments;
    private Message configReload;

    //Command Notifier
    private Message enabled;
    private Message disabled;
    private Message executedCommand;
    private Message commandsSinceLogout;
    private Message noCommandsInSpecifiedTimespan;
    private Message logEntryFormat;
    private Message logEntryHeader;
    private Message logEntryFooter;

    private MessagesFile() {
        super("messages.yml");
    }

    public static MessagesFile getInstance() {
        return INSTANCE;
    }

    @Override
    public void setDefaults() {
        setHeader(
                "This is the Message file for all CommandNotifier messages.",
                "",
                "All messages are fully customizable and support color codes, formatting and ASCII symbols.",
                "Set the Prefix and use %prefix% to add the corresponding prefix to a message.",
                "Prepend any message with <ActionBar> to send it as an ActionBar message.",
                "Leave a message blank ('') to disable it.",
                "",
                "You can also create messages with Hover and Click events. Syntax options: (Space between comma and quote is NOT allowed)",
                " - [\"Message\",\"/Command\"]",
                " - [\"Message\",\"Hover\"]",
                " - [\"Message\",\"/Command\",\"Hover\"]",
                " - [\"Message\",\"/Command\",\"Suggest\"]",
                " - [\"Message\",\"/Command\",\"Hover\",\"Suggest\"]",
                "You can add as many events to a message as you want. Example:",
                "'%prefix% &cInvalid arguments! [\"&c&n&oHelp\",\"/commandnotifier help\",\"&aClick to get help!\"]'",
                "The \"Suggest\" tag is used if the click event should suggest the command. Default is Run.",
                "");

        prefix = (String) add("Prefix", "&7[&9CommandNotifier&7]");
        invalidPermission = new Message(add("Messages.General.InvalidPermission", "%prefix% &cYou do not have permission to do this!"));
        noCommands = new Message(add("Messages.General.NoCommands", "Unknown command. Type \"/help\" for help."));
        playerOnly = new Message(add("Messages.General.PlayerOnly", "%prefix% &cCommand can only be used as a Player!"));
        invalidArguments = new Message(add("Messages.General.InvalidArguments", "%prefix% &cInvalid arguments! [\"&c&n&oHelp\",\"/commandnotifier help\",\"&aClick to get help!\"]"));
        configReload = new Message(add("Messages.General.ConfigurationsReloaded", "%prefix% &aConfiguration files successfully reloaded!"));

        enabled = new Message(add("Messages.CommandNotifier.Enabled", "%prefix% &aCommand notifications have been enabled!"));
        disabled = new Message(add("Messages.CommandNotifier.Disabled", "%prefix% &aCommand notifications have been disabled!"));
        executedCommand = new Message(add("Messages.CommandNotifier.ExecutedCommand", "%prefix% &e%player% &aexecuted &e%command%"));
        commandsSinceLogout = new Message(add("Messages.CommandNotifier.CommandsSinceLogout", "%prefix% &eExecuted commands since you last logged out:"));
        noCommandsInSpecifiedTimespan = new Message(add("Messages.CommandNotifier.NoCommandsInSpecifiedTimespan", "%prefix% &cThere were no executed commands in the specified timespan!"));
        logEntryHeader = new Message(add("Messages.CommandNotifier.LogEntryHeader", "&e&l&m----&e Page &a%page%/%pages% &e&l&m------------"));
        logEntryFormat = new Message(add("Messages.CommandNotifier.LogEntryFormat", " &7&l● &a%timestamp% &e&l> &e%player% &aexecuted &e%command%"));
        logEntryFooter = new Message(add("Messages.CommandNotifier.LogEntryFooter", "&e&l&m----&e [\"[&9Previous&f]\",\"/commandnotifier log page previous\",\"&aClick to go to the previous page\"] &e&l&m--&e [\"[&9Next&f]\",\"/commandnotifier log page next\",\"&aClick to go to the next page\"] &e&l&m----"));
    }
}
