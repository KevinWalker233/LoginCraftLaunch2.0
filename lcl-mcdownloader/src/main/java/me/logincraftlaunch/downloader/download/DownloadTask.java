package me.logincraftlaunch.downloader.download;

import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

public abstract class DownloadTask<T> implements Callable<T> {

    @Setter @Getter private DownloadPool pool;

    protected TaskState state = TaskState.PENDING;

    private final Set<DownloadTask> child = new HashSet<>(1);

    public final T call() throws Exception {
        T t = null;
        try {
            state = TaskState.RUNNING;
            t = compute();
            if (t == null) state = TaskState.FAIL;
            else state = TaskState.SUCCESS;
        } catch (Throwable e) {
            state = TaskState.FAIL;
            throw e;
        }
        return t;
    }

    public int allCount() {
        val sum = child().stream().mapToInt(DownloadTask::allCount).sum();
        return sum == 0 ? 1 : sum;
    }

    public int doneCount() {
        return (isDone() ? 1 : 0) + child().stream().mapToInt(DownloadTask::doneCount).sum();
    }

    public long getSpeed() {
        return speed() + child().stream().mapToLong(DownloadTask::getSpeed).sum();
    }

    public double getProgress() {
        val par = 1d / allCount();
        return progress() * par + child().stream().mapToDouble(it -> it.getProgress() * it.allCount() * par).sum();
    }

    public abstract String name();

    public Set<DownloadTask> child() {
        return child;
    }

    public boolean isDone() {
        return state() == TaskState.SUCCESS || state() == TaskState.FAIL;
    }

    public TaskState state() {
        return state;
    }

    /**
     * @return speed per seconds
     */
    protected long speed() {
        return 0;
    }

    protected double progress() {
        return 0;
    }

    /**
     * Task computing logic.
     *
     * @return null if fail, value if success
     * @throws Exception when failed
     */
    @NonNull
    protected abstract T compute() throws Exception;

}
