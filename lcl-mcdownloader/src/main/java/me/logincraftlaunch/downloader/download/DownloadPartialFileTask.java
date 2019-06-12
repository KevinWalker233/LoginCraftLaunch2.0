package me.logincraftlaunch.downloader.download;

import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.file.Files;

@RequiredArgsConstructor(staticName = "of")
public class DownloadPartialFileTask extends DownloadTask<File> {

    private final File target;
    private final String url;
    private final long done;

    ReadWriteTask underlying;

    @Override
    public String name() {
        return "download from " + done + "b " + url;
    }

    @Override
    protected File compute() throws Exception {
        if (!Files.exists(target.toPath())) {
            Files.createFile(target.toPath());
        }
        val url = new URL(this.url);
        val conn = ((HttpURLConnection) url.openConnection());
        conn.setRequestProperty("Range", "bytes=" + done + "-");
        val len = conn.getContentLengthLong();
        val in = Channels.newChannel(conn.getInputStream());
        val raf = new RandomAccessFile(target, "rw");
        val out = raf.getChannel().position(done);
        underlying = ReadWriteTask.of(in, out, len);
        child().add(underlying);
        getPool().submit(underlying).get();
        return target;
    }

}
