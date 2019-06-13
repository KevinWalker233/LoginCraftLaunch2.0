package me.logincraftlaunch.downloader.provider;

import com.google.common.collect.Streams;

public interface ApiProvider {

    String versionManifestUrl();

    String assetsUrl();

    String mapUrl(String url);

}
