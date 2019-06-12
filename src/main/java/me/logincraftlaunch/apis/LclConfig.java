package me.logincraftlaunch.apis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.var;
import me.logincraftlaunch.utils.FileUtil;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Created by KevinWalker on 2019/1/29.
 * 配置文件系统
 */
public class LclConfig {

    public static LclConfig instance;

    public LinkedHashMap<String, Object> lclConfigMap = new LinkedHashMap<>();

    public LclConfig() {
        LclConfig.instance = this;
        load();
    }

    /**
     * 载入/重载 配置文件
     */
    @SneakyThrows
    public void load() {
        var config = new File(FileUtil.getBaseDir(), "/LcLConfig");
        if (!config.exists()) config.mkdir();
        var configFile = new File(FileUtil.getBaseDir(), "/LcLConfig/config.json");
        if (!configFile.exists()) {
            FileUtil.saveResource("config.json",configFile);
        }
        var objectMapper = new ObjectMapper();
        var configJson = objectMapper.readTree(FileUtil.toString(configFile, "utf-8"));
        Iterator<String> iterator = configJson.fieldNames();
        String key;
        Object value;
        while (iterator.hasNext()) {
            key = iterator.next();
            value = configJson.get(key).asText();
            lclConfigMap.put(key, value);
        }
    }

    /**
     * 储存配置文件
     *
     * @return 储存的内容
     */
    @SneakyThrows
    public String save() {
        var df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        lclConfigMap.put("saveTime", df.format(System.currentTimeMillis()));
        var objectMapper = new ObjectMapper();
        var configJson = objectMapper.writeValueAsString(lclConfigMap);
        objectMapper.writeValue(new File(FileUtil.getBaseDir(), "/LcLConfig/config.json"),configJson.toString().getBytes(Charset.forName("utf-8")));
        FileUtil.write(configJson.toString().getBytes(Charset.forName("utf-8")),
                new File(FileUtil.getBaseDir(), "/LcLConfig/config.json"));
        return configJson.toString();
    }

    /**
     * 添加配置
     *
     * @param configName 配置名称
     * @param value      配置内容
     */
    public void addConfig(String configName, Object value) {
        lclConfigMap.put(configName, value);
    }

    /**
     * 删除配置
     *
     * @param configName 配置名称
     */
    public void deleteConfig(String configName) {
        lclConfigMap.remove(configName);
    }
}
