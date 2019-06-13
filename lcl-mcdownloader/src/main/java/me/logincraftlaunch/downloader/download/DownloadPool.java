package me.logincraftlaunch.downloader.download;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.val;

import java.util.concurrent.*;

public class DownloadPool extends ThreadPoolExecutor {

    public DownloadPool(int maxSize) {
        super(Math.max(maxSize, 3), Math.max(maxSize, 3), 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("lcl-mcdownloader-%d").build());
    }

    public <T> Future<T> submitNoCheck(DownloadTask<?> parent, DownloadTask<T> child) {
        parent.child().add(child);
        return submitNoCheck(child);
    }

    public <T> Future<T> submitNoCheck(Callable<T> task) {
        if (task instanceof DownloadTask) {
            ((DownloadTask<T>) task).setPool(this);
        }
        return super.submit(task);
    }

    public <T> Future<T> submit(DownloadTask<?> parent, DownloadTask<T> child) {
        parent.child().add(child);
        return submit(child);
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
                System.out.println("Task " + ((DownloadTask<T>) task).name() + " end with exception=" + t.toString() + " . STATE: " + ((DownloadTask<T>) task).state());
                t.printStackTrace();
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
            if (((DownloadFutureTask) r).getTask().state() == TaskState.SUCCESS) {
                ((DownloadFutureTask<?>) r).getTask().getThen().forEach(it -> submitNoCheck(((DownloadFutureTask) r).getTask(), it));
            }
            if (t == null) t = ((DownloadFutureTask) r).getException();
            if (t == null) {
                System.out.println("Task " + ((DownloadFutureTask) r).getTask().name() +
                        " end. STATE: " + ((DownloadFutureTask) r).getTask().state());
            } else {
                System.out.println("Task " + ((DownloadFutureTask) r).getTask().name() +
                        " end with exception=" + t.toString() + ". STATE: " + ((DownloadFutureTask) r).getTask().state());
                t.printStackTrace();
            }
        }
    }

    public static <T> DownloadTask<T> fromFuture(Future<T> future) {
        if (future instanceof DownloadFutureTask) return ((DownloadFutureTask<T>) future).getTask();
        else throw new ClassCastException(future.getClass() + " not a mcdownloader class.");
    }

    private static class DownloadFutureTask<V> extends FutureTask<V> {

        private final DownloadTask<V> task;
        private Throwable t;

        public DownloadFutureTask(DownloadTask<V> callable) {
            super(callable);
            this.task = callable;
        }

        public DownloadTask<V> getTask() {
            return task;
        }

        @Override
        protected void setException(Throwable t) {
            super.setException(t);
            this.t = t;
        }

        public Throwable getException() {
            return t;
        }

    }
}
