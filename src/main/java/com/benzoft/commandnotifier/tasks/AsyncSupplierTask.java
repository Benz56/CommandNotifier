package com.benzoft.commandnotifier.tasks;

import org.bukkit.Bukkit;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AsyncSupplierTask<T> extends AsyncTask<Supplier<T>> implements NotifiedCompletable<Consumer<T>> {

    AsyncSupplierTask(final Supplier<T> asyncTask) {
        super(asyncTask);
    }

    @Override
    public void whenComplete(final Consumer<T> syncTask) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> Optional.ofNullable(getAsyncTask().get()).ifPresent(result -> Bukkit.getScheduler().runTask(getPlugin(), () -> syncTask.accept(result))));
    }
}
