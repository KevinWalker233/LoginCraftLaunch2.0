package me.logincraftlaunch.downloader.download;

import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

@RequiredArgsConstructor(staticName = "of")
class ReadWriteTask extends DownloadTask<Boolean> {

    private final ReadableByteChannel ins;
    private final WritableByteChannel outs;
    private final long len;
    long done = 0;
    private long s1 = 0, s2 = 0;
    private boolean last;

    @Override
    protected long speed() {
        return (System.currentTimeMillis() / 1000) % 2 == 0 ? s2 : s1;
    }

    @Override
    protected double progress() {
        return len == 0 ? 0 : ((double) done) / len;
    }

    @Override
    public String name() {
        return "transfer stream idx=" + done + " length=" + len;
    }

    @Override
    protected Boolean compute() throws Exception {
        try (val in = ins;
             val out = outs) {
            val buf = ByteBuffer.allocate(1024);
            var len = 0;
            while ((len = in.read(buf)) != -1) {
                buf.flip();
                out.write(buf);
                buf.clear();
                done += len;
                val now = (System.currentTimeMillis() / 1000) % 2 == 0;
                if (now) {
                    if (!last) s1 = len;
                    else s1 += len;
                } else {
                    if (last) s2 = len;
                    else s2 += len;
                }
                last = now;
            }
        }
        return true;
    }
}
