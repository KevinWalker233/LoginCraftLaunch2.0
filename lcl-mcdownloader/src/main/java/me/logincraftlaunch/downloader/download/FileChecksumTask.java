package me.logincraftlaunch.downloader.download;

import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
import me.logincraftlaunch.downloader.DownloaderOption;
import me.logincraftlaunch.downloader.data.FileInfo;

import java.nio.file.Files;

@RequiredArgsConstructor(staticName = "of")
public class FileChecksumTask extends DownloadTask<Boolean> {

    private final DownloaderOption option;

    private final FileInfo fileInfo;

    private String actual;
    private long actualSize;

    @Override
    public String name() {
        if (option.getCheckOption() == DownloaderOption.CheckOptions.CHECK_HASH)
            return "checksum file=" + fileInfo.getFile() + " sha1=" + fileInfo.getSha1() +
                    (actual == null ? "" : " actual=" + actual);
        else
            return "checksum file=" + fileInfo.getFile() + " size=" + fileInfo.getSize() +
                    (actual == null ? "" : " actual=" + actualSize);
    }

    @Override
    protected Boolean compute() throws Exception {
        val path = fileInfo.getFile();
        if (!Files.exists(path)) throw new Exception("Not an existing file: " + fileInfo.getFile());
        if (option.getCheckOption() == DownloaderOption.CheckOptions.CHECK_HASH && fileInfo.getSha1() != null) {
            val hasher = Hashing.sha1().newHasher();
            val ins = Files.newInputStream(path);
            val buf = new byte[1024];
            var len = 0;
            while ((len = ins.read(buf)) != -1)
                hasher.putBytes(buf, 0, len);
            actual = hasher.hash().toString();
            if (actual.equalsIgnoreCase(fileInfo.getSha1()))
                return true;
            else {
                Files.delete(path);
                state = TaskState.FAIL;
                return false;
            }
        } else if (option.getCheckOption() == DownloaderOption.CheckOptions.CHECK_SIZE && fileInfo.getSize() > 0) {
            actualSize = Files.size(path);
            if (actualSize == fileInfo.getSize())
                return true;
            else {
                Files.delete(path);
                state = TaskState.FAIL;
                return false;
            }
        }
        return true;
    }
}
