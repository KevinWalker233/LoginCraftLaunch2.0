package me.logincraftlaunch.downloader.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.val;

@Data
@JsonSerialize
public class VersionManifestData {

    private Latest latest;

    private VersionInfo[] versions;


    @Data
    @JsonSerialize
    public static class Latest {
        private String release;
        private String snapshot;
    }

    @Data
    @JsonSerialize
    public static class VersionInfo {
        private String id;

    }

    public enum VersionType {

    }
}
