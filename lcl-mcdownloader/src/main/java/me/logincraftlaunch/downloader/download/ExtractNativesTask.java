package me.logincraftlaunch.downloader.download;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Iterators;
import com.google.common.collect.Streams;
import lombok.RequiredArgsConstructor;
import lombok.val;
import me.logincraftlaunch.downloader.DownloaderOption;
import me.logincraftlaunch.downloader.data.FileInfo;
import me.logincraftlaunch.downloader.data.MinecraftVersion;

import java.nio.channels.Channels;
import java.nio.file.Files;
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
        val ins = new ZipInputStream(Files.newInputStream(source));
        ZipEntry entry;
        while ((entry = ins.getNextEntry()) != null) {
            if (!excludeList.contains(entry.getName())) {
                val out = Files.newByteChannel(rootDir.resolve(entry.getName()), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                getPool().submit(this, ReadWriteTask.of(Channels.newChannel(ins), out, entry.getSize())).get();
            }
        }
        return null;
    }
}
