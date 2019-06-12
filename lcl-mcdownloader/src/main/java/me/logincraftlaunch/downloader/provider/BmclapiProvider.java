package me.logincraftlaunch.downloader.provider;

public class BmclapiProvider implements ApiProvider {
    @Override
    public String versionManifestUrl() {
        return "https://bmclapi2.bangbang93.com/mc/game/version_manifest.json";
    }
}
