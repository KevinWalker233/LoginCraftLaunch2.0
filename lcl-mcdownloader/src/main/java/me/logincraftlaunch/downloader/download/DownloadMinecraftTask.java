package me.logincraftlaunch.downloader.download;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.logincraftlaunch.downloader.DownloaderOption;
import me.logincraftlaunch.downloader.data.FileInfo;
import me.logincraftlaunch.downloader.data.MinecraftVersion;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.concurrent.Future;

@RequiredArgsConstructor(staticName = "of")
public class DownloadMinecraftTask extends DownloadTask<MinecraftVersion> {

    private final MinecraftVersion version;

    private final DownloaderOption option;

    @Override
    public String name() {
        return "download Minecraft version=" + version.getId();
    }

    @Override
    protected MinecraftVersion compute() throws Exception {
        val futures = new ArrayList<Future<?>>();
        val root = option.getRootDir();
        val versionDir = root.resolve("versions/" + version.getId() + "");
        if (!Files.isDirectory(versionDir)) Files.createDirectories(versionDir);
        val task = DownloadJsonTask.of(option, version.getUrl());
        val node = getPool().submit(this, task).get();
        if (!option.isSkipAssets()) {
            val index = node.get("assetIndex");
            val assetsIndexTask = DownloadAssetsIndexTask.of(option, version,
                    FileInfo.from(index, root.resolve("assets/indexes/" + index.get("id").asText() + ".json")));
            futures.add(getPool().submitNoCheck(this, assetsIndexTask));
        }
        val client = node.get("downloads").get("client");
        val clientTask = DownloadFileRetryingTask.of(option, FileInfo.from(client, versionDir.resolve(version.getId() + ".jar")));
        futures.add(getPool().submitNoCheck(this, clientTask));
        val libraries = node.get("libraries");
        val librariesTask = DownloadLibrariesTask.of(version, option, libraries);
        futures.add(getPool().submitNoCheck(this, librariesTask));

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Throwable ignored) {
            }
        }
        return version;
    }


}
