package me.logincraftlaunch.downloader.provider;

import com.google.common.collect.Streams;

public class BmclapiProvider implements ApiProvider {
    @Override
    public String versionManifestUrl() {
        return "https://bmclapi2.bangbang93.com/mc/game/version_manifest.json";
    }

    @Override
    public String assetsUrl() {
        return "https://bmclapi2.bangbang93.com/assets/";
    }

    @Override
    public String mapUrl(String url) {
        return url
                .replace("https://launchermeta.mojang.com", "https://bmclapi2.bangbang93.com")
                .replace("https://launcher.mojang.com", "https://bmclapi2.bangbang93.com")
                .replace("https://libraries.minecraft.net", "https://bmclapi2.bangbang93.com/libraries")
                .replaceFirst("https?://files\\.minecraftforge\\.net/maven", "https://bmclapi2.bangbang93.com/maven")
                .replace("http://dl.liteloader.com/versions/versions.json", "https://bmclapi2.bangbang93.com/maven/com/mumfrey/liteloader/versions.json")
                .replace("http://dl.liteloader.com/versions", "https://bmclapi2.bangbang93.com/maven")
                .replace("https://authlib-injector.yushi.moe", "https://bmclapi2.bangbang93.com/mirrors/authlib-injector");
    }
}
