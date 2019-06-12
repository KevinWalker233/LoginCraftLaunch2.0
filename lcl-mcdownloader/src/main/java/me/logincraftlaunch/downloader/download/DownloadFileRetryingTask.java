package me.logincraftlaunch.downloader.download;

import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor(staticName = "of")
public class DownloadFileRetryingTask extends DownloadTask<File> {

    private final String url;

    private final File target;

    private final int maxRetry;

    private int retry = 0;

    @Override
    public String name() {
        return "max=" + maxRetry + " retry=" + retry + " download " + url;
    }

    @Override
    protected File compute() throws Exception {
        val path = target.toPath();
        var done = 0L;
        while (retry < maxRetry) {
            val task = DownloadPartialFileTask.of(target, url, done);
            child().add(task);
            try {
                return getPool().submit(task).get();
            } catch (InterruptedException e) {
                Files.deleteIfExists(path);
                return null;
            } catch (ExecutionException e) {
                // todo localized
                System.err.println("Download " + url + " failed: " + unwrap(e).getLocalizedMessage());
                done += task.underlying.done;
                child().clear();
            }
            retry++;
        }
        return null;
    }

    private Throwable unwrap(ExecutionException e) {
        val cause = e.getCause();
        if (cause instanceof ExecutionException) return unwrap(((ExecutionException) cause));
        else return cause;
    }

}
