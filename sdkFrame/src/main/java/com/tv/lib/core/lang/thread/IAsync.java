package com.tv.lib.core.lang.thread;

public interface IAsync {
    void exec(Runnable runnable, final long delay);

    void exec(TaskHelper.Task task, long delay);

    void exec(TaskHelper.Task task, long backgroundDelay, long uiDelay);

    void removeMessages(int what, Object object);
}
