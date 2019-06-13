package me.logincraftlaunch.downloader.download;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.val;
import me.logincraftlaunch.api.Api;
import me.logincraftlaunch.downloader.DownloaderOption;
import me.logincraftlaunch.downloader.data.FileInfo;
import me.logincraftlaunch.downloader.data.MinecraftVersion;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.Future;

@RequiredArgsConstructor(staticName = "of")
public class DownloadAssetsIndexTask extends DownloadTask<Path> {

    private final DownloaderOption option;

    private final MinecraftVersion version;

    private final FileInfo fileInfo;

    @Override
    public String name() {
        return "download assets for " + version.getId() + " from " + fileInfo.getUrl();
    }

    @Override
    protected Path compute() throws Exception {
        val futures = new ArrayList<Future<?>>();
        val file = getPool().submit(this, DownloadFileRetryingTask.of(option, fileInfo)).get();
        val node = Api.mapper().readTree(file.toFile());
        val rootPath = option.getRootDir().resolve("assets").resolve("objects");
        if (!Files.isDirectory(rootPath)) Files.createDirectories(rootPath);
        val baseUrl = option.getProvider().assetsUrl();
        for (JsonNode object : node.get("objects")) {
            val hash = object.get("hash").asText();
            val len = object.get("size").asLong();
            val path = hash.substring(0, 2) + "/" + hash;
            val dest = rootPath.resolve(path);
            val url = baseUrl + path;
            val info = FileInfo.of(dest, len, url, hash);
            futures.add(getPool().submitNoCheck(this, DownloadFileRetryingTask.of(option, info)));
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Throwable ignored) {
            }
        }
        return file;
    }

}
