package com.benzoft.commandnotifier.tasks;

public interface NotifiedCompletable<T> {

    void whenComplete(T syncTask);
}
