package me.logincraftlaunch.launch.versions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.File;
import java.io.IOException;

@Data public class Version {

    public String minecraftArguments;
    public String mainClass;
    public String time;
    public String id;
    public String type;
    public String releaseTime;
    public String assets;
    public String minimumLauncherVersion;

    public JsonNode versionJsonNode;

    public Version(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.versionJsonNode = mapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        readVersionJson();
    }

    public Version(File versionfile) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.versionJsonNode = mapper.readTree(versionfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        readVersionJson();
    }

    /**
     * 解析版本json的基本信息
     */
    public void readVersionJson() {
        this.minecraftArguments = this.versionJsonNode.path("minecraftArguments").textValue();
        this.mainClass = this.versionJsonNode.path("mainClass").textValue();
        this.time = this.versionJsonNode.path("time").textValue();
        this.id = this.versionJsonNode.path("id").textValue();
        this.type = this.versionJsonNode.path("type").textValue();
        this.releaseTime = this.versionJsonNode.path("releaseTime").textValue();
        this.assets = this.versionJsonNode.path("assets").textValue();
        this.minimumLauncherVersion = this.versionJsonNode.path("minimumLauncherVersion").textValue();
    }
}

