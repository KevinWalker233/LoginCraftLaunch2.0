package me.logincraftlaunch.downloader.download;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Function;
import com.google.common.collect.Streams;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import me.logincraftlaunch.api.Api;
import me.logincraftlaunch.downloader.data.MinecraftVersion;
import me.logincraftlaunch.downloader.provider.ApiProvider;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "of")
public class FetchVersionManifestTask extends DownloadTask<List<MinecraftVersion>> {
    private final ApiProvider provider;

    @Override
    public String name() {
        return "fetch minecraft manifest";
    }

    @Override
    protected List<MinecraftVersion> compute() throws Exception {
        val task = DownloadJsonTask.of(provider.versionManifestUrl());
        child().add(task);
        val node = getPool().submit(task).get();
        return Streams.stream(node.get("versions").elements()).map(new Function<JsonNode, MinecraftVersion>() {
            @SneakyThrows
            public MinecraftVersion apply(JsonNode input) {
                return Api.mapper().treeToValue(input, MinecraftVersion.class);
            }
        }).collect(Collectors.toList());
    }

}
