package com.benzoft.commandnotifier.persistence.database;

import com.benzoft.commandnotifier.LogContainer;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public interface Database {

    void openConnection();

    void closeConnection();

    void logCommand(final Player player, final String parentCommand, final String fullCommand);

    void retrieveLogs(long from, final Consumer<LogContainer> onRetrieve);
}
