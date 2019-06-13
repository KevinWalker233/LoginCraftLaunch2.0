package me.logincraftlaunch.downloader.download;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
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
            if (!checkRules(library)) continue;
            // download artifact
            if (library.get("downloads").has("artifact")) {
                val node = library.get("downloads").get("artifact");
                val info = FileInfo.from(node, baseDir.resolve(node.get("path").asText()));
                futures.add(getPool().submitNoCheck(this, DownloadFileRetryingTask.of(option, info)));
            }
            // download natives
            if (library.has("natives") && library.get("natives").has(option.getPlatform())) {
                val natives = library.get("natives").get(option.getPlatform()).asText().replace("${arch}", option.getArch());
                val classifiers = library.get("downloads").get("classifiers");
                if (classifiers.has(natives)) {
                    val nativeNode = classifiers.get(natives);
                    val nativeInfo = FileInfo.from(nativeNode, baseDir.resolve(nativeNode.get("path").asText()));
                    val task = DownloadFileRetryingTask.of(option, nativeInfo);
                    if (library.has("extract")) {
                        task.then(ExtractNativesTask.of(option, version, nativeInfo, library.get("extract").get("exclude")));
                    }
                    futures.add(getPool().submitNoCheck(this, task));
                }
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

    private boolean checkRules(JsonNode library) {
        if (!library.has("rules")) return true;
        else {
            val rules = library.get("rules");
            var ret = false;
            for (JsonNode rule : rules) {
                val action = rule.get("action").asText();
                val allow = action.equals("allow");
                if (rule.has("os")) {
                    ret = allow ^ rule.get("os").get("name").asText().equals(option.getPlatform());
                } else ret = allow;
            }
            return ret;
        }
    }
}
