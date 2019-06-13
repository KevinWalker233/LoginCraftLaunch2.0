package me.logincraftlaunch.downloader.download;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.val;
import me.logincraftlaunch.api.Api;
import me.logincraftlaunch.downloader.DownloaderOption;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;

@RequiredArgsConstructor(staticName = "of")
public class DownloadJsonTask extends DownloadTask<JsonNode> {

    private final DownloaderOption option;

    private final String url;

    @Override
    public String name() {
        return "download js from " + option.getProvider().mapUrl(url);
    }

    @Override
    protected JsonNode compute() throws Exception {
        val conn = (HttpURLConnection) new URL(option.getProvider().mapUrl(url)).openConnection();
        val len = conn.getContentLengthLong();
        val in = Channels.newChannel(conn.getInputStream());
        val bs = new ByteArrayOutputStream();
        val out = Channels.newChannel(bs);
        val task = ReadWriteTask.of(in, out, len);
        getPool().submit(this, task).get();
        return Api.mapper().readTree(bs.toByteArray());
    }

}
