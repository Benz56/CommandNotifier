package com.benzoft.commandnotifier.persistence.database;

import com.benzoft.commandnotifier.LogContainer;
import com.benzoft.commandnotifier.persistence.persistenceobjects.LogEntry;
import com.benzoft.commandnotifier.tasks.AsyncSupplierTask;
import com.benzoft.commandnotifier.tasks.AsyncTask;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLite implements Database {

    private final static String DATABASE_NAME = "log";
    private final Plugin plugin;
    private Connection connection;


    public SQLite(final Plugin plugin) {
        this.plugin = plugin;
    }

    private void verifyTables() {
        try {
            final String logsTable = "CREATE TABLE IF NOT EXISTS " + DATABASE_NAME + " (" +
                    "Id              INTEGER      PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "Timestamp       TIMESTAMP    NOT NULL," +
                    "UUID            CHAR(36)     NOT NULL," +
                    "Username        VARCHAR(16)  NOT NULL," +
                    "ParentCommand   VARCHAR(255) NOT NULL," +
                    "ExecutedCommand TEXT         NOT NULL" +
                    ")";

            final PreparedStatement preparedStatement = connection.prepareStatement(logsTable);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void openConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + File.separator + DATABASE_NAME + ".sqlite");
            verifyTables();
        } catch (final ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeConnection() {
        if (connection == null) return;
        try {
            connection.close();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logCommand(final Player player, final String parentCommand, final String fullCommand) {
        AsyncTask.supplyAsync(() -> {
            try (final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO " + DATABASE_NAME + " (Timestamp, UUID, Username, ParentCommand, ExecutedCommand) VALUES (?,?,?,?,?)")) {
                preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                preparedStatement.setString(2, player == null ? "Console" : player.getUniqueId().toString());
                preparedStatement.setString(3, player == null ? "Console" : player.getName());
                preparedStatement.setString(4, parentCommand);
                preparedStatement.setString(5, fullCommand.startsWith("/") ? fullCommand : "/" + fullCommand);
                preparedStatement.executeUpdate();
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }).complete();
    }

    @Override
    public AsyncSupplierTask<LogContainer> retrieveLogs(final long from) {
        return AsyncTask.supplyAsync(() -> {
            try (final PreparedStatement preparedStatement = connection.prepareStatement("SELECT Timestamp, Username, ExecutedCommand FROM " + DATABASE_NAME + " WHERE Timestamp >= " + from + " ORDER BY Timestamp DESC")) {
                final List<LogEntry> entries = new ArrayList<>();
                final ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next())
                    entries.add(new LogEntry(resultSet.getTimestamp("Timestamp"), resultSet.getString("Username"), resultSet.getString("ExecutedCommand")));
                return new LogContainer(entries);
            } catch (final SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

}
