package me.logincraftlaunch.downloader;

import lombok.Builder;
import lombok.Data;
import me.logincraftlaunch.downloader.provider.ApiProvider;

import java.nio.file.Path;

@Builder
@Data
public class DownloaderOption {

    private final ApiProvider provider;

    private final int poolSize;

    private final int maxRetry;

    private final Path rootDir;

    private final boolean separateVersion;

    private final boolean skipAssets;

    private final CheckOptions checkOption;

    /**
     * should be windows / osx / linux
     */
    private final String platform;

    public enum CheckOptions {
        CHECK_SIZE, CHECK_HASH
    }

}
