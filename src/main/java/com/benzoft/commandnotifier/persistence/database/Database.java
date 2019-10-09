package com.benzoft.commandnotifier.persistence.database;

import com.benzoft.commandnotifier.LogContainer;
import com.benzoft.commandnotifier.tasks.AsyncSupplierTask;
import org.bukkit.entity.Player;

public interface Database {

    void openConnection();

    void closeConnection();

    void logCommand(final Player player, final String parentCommand, final String fullCommand);

    AsyncSupplierTask<LogContainer> retrieveLogs(long from);
}
