package me.logincraftlaunch.downloader;

import lombok.val;
import me.logincraftlaunch.downloader.data.MinecraftVersion;
import me.logincraftlaunch.downloader.download.DownloadMinecraftTask;
import me.logincraftlaunch.downloader.download.DownloadPool;
import me.logincraftlaunch.downloader.download.FetchVersionManifestTask;
import me.logincraftlaunch.downloader.provider.BmclapiProvider;

import java.io.File;
import java.util.List;
import java.util.concurrent.Future;

public class MinecraftDownloader {

    public MinecraftDownloader(DownloaderOption option) {
        this.option = option;
        this.pool = new DownloadPool(option.getPoolSize());
    }

    private final DownloaderOption option;

    private final DownloadPool pool;

    public Future<List<MinecraftVersion>> getVersionList() {
        return pool.submit(FetchVersionManifestTask.of(option));
    }

    public Future<MinecraftVersion> downloadMinecraft(MinecraftVersion version) {
        return pool.submit(DownloadMinecraftTask.of(version, option));
    }

    public void shutdown() {
        pool.shutdown();
    }

    public static void main(String[] args) {
        try {
            val downloader = new MinecraftDownloader(DownloaderOption.builder()
                    .poolSize(32)
                    .checkOption(DownloaderOption.CheckOptions.CHECK_HASH)
                    .maxRetry(3)
                    .timeout(7000)
                    .platform("windows")
                    .arch("64")
                    .provider(new BmclapiProvider())
                    .separateVersion(false)
                    .skipAssets(false)
                    .rootDir(new File("C:/Users/csh20/Desktop/新建文件夹/.minecraft").toPath())
                    .build());
            val versions = downloader.getVersionList().get();
            downloader.downloadMinecraft(versions.get(130)).get();
            downloader.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
