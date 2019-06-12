package me.logincraftlaunch.downloader.provider;

public class MojangProvider implements ApiProvider {
    @Override
    public String versionManifestUrl() {
        return "https://launchermeta.mojang.com/mc/game/version_manifest.json";
    }
}
