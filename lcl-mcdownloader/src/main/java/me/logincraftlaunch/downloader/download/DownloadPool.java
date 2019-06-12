package me.logincraftlaunch.downloader.download;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.val;

import java.util.concurrent.*;

public class DownloadPool extends ThreadPoolExecutor {

    public DownloadPool() {
        super(1, 32, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("lcl-mcdownloader-%d").build());
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        if (task instanceof DownloadTask) {
            ((DownloadTask<T>) task).setPool(this);
        }
        if (task instanceof DownloadTask && Thread.currentThread().getName().startsWith("lcl-mcdownloader")) {
            try {
                System.out.println("Task " + ((DownloadTask<T>) task).name() + " start.");
                val future = Futures.immediateCheckedFuture(task.call());
                System.out.println("Task " + ((DownloadTask<T>) task).name() + " end. STATE: " + ((DownloadTask<T>) task).state());
                return future;
            } catch (Exception t) {
                Future<T> future = Futures.immediateFailedCheckedFuture(t);
                System.out.println("Task " + ((DownloadTask<T>) task).name() + " end with exception. STATE: " + ((DownloadTask<T>) task).state());
                return future;
            }
        } else return super.submit(task);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        if (callable instanceof DownloadTask)
            return new DownloadFutureTask<>(((DownloadTask<T>) callable));
        else
            return new FutureTask<>(callable);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        if (r instanceof DownloadFutureTask)
            System.out.println("Task " + ((DownloadFutureTask) r).getTask().name() + " start.");
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        if (r instanceof DownloadFutureTask) {
            if (t == null)
                System.out.println("Task " + ((DownloadFutureTask) r).getTask().name() +
                        " end. STATE: " + ((DownloadFutureTask) r).getTask().state());
            else
                System.out.println("Task " + ((DownloadFutureTask) r).getTask().name() +
                        " end with exception. STATE: " + ((DownloadFutureTask) r).getTask().state());
        }
    }

    private static class DownloadFutureTask<V> extends FutureTask<V> {

        private final DownloadTask<V> task;

        public DownloadFutureTask(DownloadTask<V> callable) {
            super(callable);
            this.task = callable;
        }

        public DownloadTask<V> getTask() {
            return task;
        }

    }
}
