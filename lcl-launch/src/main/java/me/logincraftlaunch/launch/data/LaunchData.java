package me.logincraftlaunch.launch.data;

import lombok.Data;

@Data public class LaunchData {
    public String gamePath;
    public String javaPath;
    public String memory;
    public String clientPath;
    public String libraryPath;
    public String jvmOptions;//jvm参数
    public String mainClass;
    public String authPlayerName;
    public String versionName;
    public String gameDirectory;
    public String assetsRoot;
    public String assetsIndexName;
    public String authUuid;
    public String authAccessToken;
    public String userType;
    public String versionType;
    public String otherOptions;//高版本rules的额外参数
    public String server;
    public String post;
}
