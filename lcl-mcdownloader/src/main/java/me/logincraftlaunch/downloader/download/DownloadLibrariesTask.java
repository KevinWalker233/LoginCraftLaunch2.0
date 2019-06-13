package me.logincraftlaunch.downloader.download;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.val;
import me.logincraftlaunch.downloader.DownloaderOption;
import me.logincraftlaunch.downloader.data.FileInfo;
import me.logincraftlaunch.downloader.data.MinecraftVersion;

import java.util.ArrayList;
import java.util.concurrent.Future;

@RequiredArgsConstructor(staticName = "of")
public class DownloadLibrariesTask extends DownloadTask<Boolean> {

    private final MinecraftVersion version;

    private final DownloaderOption option;

    private final JsonNode libraries;

    @Override
    public String name() {
        return "download libraries for " + version.getId();
    }

    @Override
    protected Boolean compute() throws Exception {
        val futures = new ArrayList<Future<?>>();
        val baseDir = option.getRootDir().resolve("libraries");
        for (JsonNode library : libraries) {
            // download artifact
            val node = library.get("downloads").get("artifact");
            val info = FileInfo.from(node, baseDir.resolve(node.get("path").asText()));
            futures.add(getPool().submitNoCheck(this, DownloadFileRetryingTask.of(option, info)));
            if (node.has("natives")) {
                val natives = node.get("natives").get(option.getPlatform()).asText();
                val classifiers = node.get("classifiers").get(natives);
                val nativeInfo = FileInfo.from(classifiers, baseDir.resolve(classifiers.get("path").asText()));
                val task = DownloadFileRetryingTask.of(option, nativeInfo);
                if (node.has("extract"))
                    task.then(ExtractNativesTask.of(option, version, nativeInfo, node.get("extract").get("exclude")));
                futures.add(getPool().submitNoCheck(this, task));
            }
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Throwable ignored) {
            }
        }
        return true;
    }
}
