package com.benzoft.commandnotifier.tasks;

import org.bukkit.Bukkit;

public class AsyncRunnableTask extends AsyncTask<Runnable> implements NotifiedCompletable<Runnable>, SilentCompletable {

    AsyncRunnableTask(final Runnable asyncTask) {
        super(asyncTask);
    }

    @Override
    public void whenComplete(final Runnable syncTask) {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            getAsyncTask().run();
            Bukkit.getScheduler().runTask(getPlugin(), syncTask);
        });
    }

    @Override
    public void complete() {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), getAsyncTask());
    }
}
