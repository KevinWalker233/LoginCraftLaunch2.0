package me.logincraftlaunch.downloader.data;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Wither;

import java.nio.file.Path;

@AllArgsConstructor(staticName = "of")
@Data
@Wither
public class FileInfo {

    private Path file;

    private long size;

    private String url;

    private String sha1;

    @SneakyThrows
    public static FileInfo from(JsonNode node, Path file) {
        return FileInfo.of(file, node.get("size").asLong(0), node.get("url").asText(), node.get("sha1").asText());
    }

}
