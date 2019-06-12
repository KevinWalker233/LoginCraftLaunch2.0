package me.logincraftlaunch.api;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Task {

    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(4);

    public static ScheduledExecutorService executor() {
        return EXECUTOR;
    }

}
