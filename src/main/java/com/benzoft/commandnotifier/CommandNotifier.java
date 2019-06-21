package com.benzoft.commandnotifier;

import com.benzoft.commandnotifier.commands.CommandRegistry;
import com.benzoft.commandnotifier.events.EventRegistry;
import com.benzoft.commandnotifier.persistence.ConfigFile;
import com.benzoft.commandnotifier.persistence.MessagesFile;
import com.benzoft.commandnotifier.persistence.UserdataFile;
import com.benzoft.commandnotifier.persistence.database.Database;
import com.benzoft.commandnotifier.persistence.database.SQLite;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CommandNotifier extends JavaPlugin {

    @Getter
    private Database logDatabase;

    @Override
    public void onEnable() {
        EventRegistry.registerEvents(this, UserdataFile.getInstance());
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> UserdataFile.getInstance().save(), 6000, 6000); // Save userdata file every five minutes.
        CommandRegistry.registerCommands(this);
        MessagesFile.getInstance();
        ConfigFile.getInstance();
        logDatabase = new SQLite(this);
        logDatabase.openConnection();
    }

    @Override
    public void onDisable() {
        UserdataFile.getInstance().save();
        logDatabase.closeConnection();
    }
}
