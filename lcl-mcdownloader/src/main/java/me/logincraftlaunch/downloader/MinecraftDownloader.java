package me.logincraftlaunch.downloader;

import lombok.RequiredArgsConstructor;
import me.logincraftlaunch.downloader.data.MinecraftVersion;
import me.logincraftlaunch.downloader.download.DownloadPool;
import me.logincraftlaunch.downloader.download.FetchVersionManifestTask;
import me.logincraftlaunch.downloader.provider.ApiProvider;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

@RequiredArgsConstructor
public class MinecraftDownloader {

    private final File rootDir;

    private final DownloadPool pool = new DownloadPool();

    public Future<List<MinecraftVersion>> getVersionList(ApiProvider provider) {
        return pool.submit(FetchVersionManifestTask.of(provider));
    }

}
