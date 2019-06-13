package me.logincraftlaunch.downloader.download;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Iterators;
import com.google.common.collect.Streams;
import com.google.common.io.ByteStreams;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.val;
import me.logincraftlaunch.downloader.DownloaderOption;
import me.logincraftlaunch.downloader.data.FileInfo;
import me.logincraftlaunch.downloader.data.MinecraftVersion;

import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RequiredArgsConstructor(staticName = "of")
public class ExtractNativesTask extends DownloadTask<Boolean> {

    private final DownloaderOption option;

    private final MinecraftVersion version;

    private final FileInfo fileInfo;

    private final JsonNode exclude;

    @Override
    public String name() {
        return "extract natives for " + fileInfo.getFile();
    }

    @Override
    protected Boolean compute() throws Exception {
        val rootDir = option.getRootDir().resolve("versions").resolve(version.getId()).resolve(version.getId() + "-natives");
        val excludeList = Streams.stream(exclude.iterator()).map(JsonNode::asText).collect(Collectors.toList());
        val source = fileInfo.getFile();
        @Cleanup val ins = new ZipInputStream(Files.newInputStream(source));
        ZipEntry entry;
        while ((entry = ins.getNextEntry()) != null) {
            ZipEntry finalEntry = entry;
            if (excludeList.stream().noneMatch(it -> finalEntry.getName().startsWith(it))) {
                val target = rootDir.resolve(entry.getName());
                createFile(target);
                val out = Files.newByteChannel(target, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                ByteStreams.copy(Channels.newChannel(ins), out);
            }
        }
        return true;
    }

    private void createFile(Path file) throws Exception {
        if (!Files.exists(file)) {
            val parent = file.getParent();
            if (!Files.exists(parent)) Files.createDirectories(parent);
            Files.createFile(file);
        }
    }
}
