package com.benzoft.commandnotifier.utils;

import org.bukkit.Bukkit;

import java.util.Arrays;

public final class Debug {

    public static void print(final Object... o) {
        Bukkit.getServer().getLogger().info(Arrays.toString(o));
    }
}