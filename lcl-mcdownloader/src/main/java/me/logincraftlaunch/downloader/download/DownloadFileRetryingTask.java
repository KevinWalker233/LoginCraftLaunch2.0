package me.logincraftlaunch.downloader.download;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import lombok.var;
import me.logincraftlaunch.downloader.DownloaderOption;
import me.logincraftlaunch.downloader.data.FileInfo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor(staticName = "of")
public class DownloadFileRetryingTask extends DownloadTask<Path> {

    private final DownloaderOption option;

    private final FileInfo fileInfo;

    private int retry = 0;

    private boolean skip = false;

    @Override
    public String name() {
        if (skip)
            return "skipped";
        else
            return "max=" + option.getMaxRetry() + " retry=" + retry + " download " + fileInfo.getUrl();
    }

    @Override
    protected Path compute() throws Exception {
        if (skip = checksum()) return fileInfo.getFile();
        val path = fileInfo.getFile();
        var done = 0L;
        while (retry < option.getMaxRetry()) {
            val task = DownloadPartialFileTask.of(option, fileInfo, done);
            try {
                val file = getPool().submit(this, task).get();
                if (!checksum()) throw new ExecutionException(new Throwable("Bad checksum"));
                return file;
            } catch (InterruptedException e) {
                Files.deleteIfExists(path);
                throw e;
            } catch (ExecutionException e) {
                // todo localized
                System.err.println("Download " + fileInfo.getUrl() + " failed: " + unwrap(e).getLocalizedMessage());
                done += task.underlying == null ? 0 : task.underlying.done;
                child().clear();
            }
            retry++;
        }
        Files.deleteIfExists(path);
        return null;
    }

    @SneakyThrows
    private boolean checksum() {
        if (!Files.exists(fileInfo.getFile()) || Files.size(fileInfo.getFile()) == 0) return false;
        val task = FileChecksumTask.of(option, fileInfo);
        return getPool().submit(this, task).get();
    }

    private Throwable unwrap(ExecutionException e) {
        val cause = e.getCause();
        if (cause instanceof ExecutionException) return unwrap(((ExecutionException) cause));
        else return cause;
    }

}
