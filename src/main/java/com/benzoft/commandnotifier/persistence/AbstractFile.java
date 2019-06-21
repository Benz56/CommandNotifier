package com.benzoft.commandnotifier.persistence;

import com.benzoft.commandnotifier.CommandNotifier;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter(AccessLevel.PROTECTED)
public abstract class AbstractFile {

    private final File file;
    private YamlConfiguration config;

    AbstractFile(final String name) {
        file = new File(CommandNotifier.getPlugin(CommandNotifier.class).getDataFolder(), name);
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (final IOException ignored) {
            }
        }
        reload();
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
        setDefaults();
        save();
    }

    Object add(final String path, final Object value) {
        if (config.contains(path)) return config.get(path);
        config.set(path, value);
        return value;
    }

    Object conditionalAdd(final String path, final Object value, final boolean canAdd) {
        if (config.contains(path) || !canAdd) return config.get(path);
        config.set(path, value);
        return value;
    }

    boolean containsSection(final String path) {
        return config.contains(path);
    }

    void setHeader(final String... lines) {
        config.options().header(String.join("\n", lines) + "\n");
    }

    public void save() {
        try {
            config.save(file);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public abstract void setDefaults();
}
