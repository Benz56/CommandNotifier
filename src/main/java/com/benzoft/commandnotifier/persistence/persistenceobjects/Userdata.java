package com.benzoft.commandnotifier.persistence.persistenceobjects;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

@Getter
public class Userdata {

    @Getter(AccessLevel.NONE)
    private final ConfigurationSection configurationSection;
    private final long lastLogin;
    private final long lastLogout;
    private final UUID uuid;
    private boolean enabled;

    public Userdata(final ConfigurationSection configurationSection) {
        this.configurationSection = configurationSection;
        configurationSection.set("LastLogin", System.currentTimeMillis());
        lastLogin = configurationSection.getLong("LastLogin");
        lastLogout = configurationSection.getLong("LastLogout", System.currentTimeMillis());
        uuid = UUID.fromString(configurationSection.getName());
        enabled = configurationSection.getBoolean("Enabled", true);
    }

    public void toggleEnabled() {
        configurationSection.set("Enabled", enabled = !enabled);
    }

    public void setLastLogout(final long logout) {
        configurationSection.set("LastLogout", logout);
    }
}
