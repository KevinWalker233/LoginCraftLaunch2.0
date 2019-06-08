package me.logincraftlaunch.apis;

import me.logincraftlaunch.utils.FileUtil;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by KevinWalker on 2019/1/29.
 * 配置文件系统
 */
public class LclConfig {

    public static LclConfig instance;

    public Map<String, Object> lclConfigMap = new HashMap<>();

    public LclConfig() {
        LclConfig.instance = this;
        load();
    }

    /**
     * 载入/重载 配置文件
     */
    public void load() {
        File config = new File(FileUtil.getBaseDir(), "/LcLConfig");
        if (!config.exists()) config.mkdir();
        File configFile = new File(FileUtil.getBaseDir(), "/LcLConfig/config.json");
        if (!configFile.exists()) {
            FileUtil.saveResource("config.json",configFile);
        }
        JSONObject configJson = new JSONObject(FileUtil.toString(configFile, "utf-8"));
        Iterator<?> iterator = configJson.keys();
        String key;
        Object value;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            value = configJson.get(key);
            lclConfigMap.put(key, value);
        }
    }

    /**
     * 储存配置文件
     *
     * @return 储存的内容
     */
    public String save() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        lclConfigMap.put("saveTime", df.format(System.currentTimeMillis()));
        JSONObject configJson = new JSONObject(lclConfigMap);
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
