package me.logincraftlaunch.downloader.download;

import lombok.RequiredArgsConstructor;
import lombok.val;
import me.logincraftlaunch.downloader.DownloaderOption;
import me.logincraftlaunch.downloader.data.FileInfo;

import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;

@RequiredArgsConstructor(staticName = "of")
public class DownloadPartialFileTask extends DownloadTask<Path> {

    private final DownloaderOption option;

    private final FileInfo fileInfo;
    private final long done;

    ReadWriteTask underlying;

    @Override
    public String name() {
        return "download from " + done + "b " + option.getProvider().mapUrl(fileInfo.getUrl());
    }

    @Override
    protected Path compute() throws Exception {
        if (!Files.exists(fileInfo.getFile())) {
            Files.createFile(fileInfo.getFile());
        }
        val url = new URL(option.getProvider().mapUrl(fileInfo.getUrl()));
        val conn = ((HttpURLConnection) url.openConnection());
        conn.setRequestProperty("Range", "bytes=" + done + "-");
        val len = conn.getContentLengthLong();
        val in = Channels.newChannel(conn.getInputStream());
        val raf = new RandomAccessFile(fileInfo.getFile().toFile(), "rw");
        val out = raf.getChannel().position(done);
        underlying = ReadWriteTask.of(in, out, len);
        getPool().submit(this, underlying).get();
        return fileInfo.getFile();
    }

}
