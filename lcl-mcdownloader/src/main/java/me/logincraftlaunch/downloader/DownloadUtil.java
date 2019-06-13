package me.logincraftlaunch.downloader;

import lombok.experimental.UtilityClass;
import me.logincraftlaunch.downloader.download.DownloadPool;
import me.logincraftlaunch.downloader.download.DownloadTask;

import java.util.concurrent.Future;

@UtilityClass
public class DownloadUtil {

    public <T> DownloadTask<T> fromFuture(Future<T> future) {
        return DownloadPool.fromFuture(future);
    }

}
