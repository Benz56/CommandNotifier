package com.benzoft.commandnotifier.tasks;

import com.benzoft.commandnotifier.CommandNotifier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

import java.util.function.Supplier;

@Getter(AccessLevel.PROTECTED)
@RequiredArgsConstructor
public abstract class AsyncTask<R> {

    private final Plugin plugin = CommandNotifier.getPlugin(CommandNotifier.class);
    private final R asyncTask;

    public static <T> AsyncSupplierTask<T> supplyAsync(final Supplier<T> asyncTask) {
        return new AsyncSupplierTask<>(asyncTask);
    }

    public static AsyncRunnableTask supplyAsync(final Runnable asyncTask) {
        return new AsyncRunnableTask(asyncTask);
    }
}
