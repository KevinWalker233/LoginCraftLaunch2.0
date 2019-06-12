package me.logincraftlaunch.apis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.var;
import me.logincraftlaunch.utils.FileUtil;

import java.io.File;
import java.util.*;

/**
 * Created by KevinWalker on 2019/1/29.
 * 语言文件系统
 */
public class LclLocale {

    public static LclLocale instance;

    public Map<String, String> lclLocaleMap = new HashMap<>();

    private static List<String> langs = new ArrayList<String>() {{
        add("zh");
        add("en");
    }};

    public LclLocale() {
        LclLocale.instance = this;
        load();
    }

    /**
     * 载入/重载 语言文件
     */
    @SneakyThrows
    public void load() {
        var langFile = new File(FileUtil.getBaseDir(), "/LcLConfig/lang");
        var zhLangFile = new File(FileUtil.getBaseDir(), "/LcLConfig/lang/zh.json");
        var enLangFile = new File(FileUtil.getBaseDir(), "/LcLConfig/lang/en.json");

        if (!langFile.exists()) langFile.mkdir();

        if (!zhLangFile.exists()) {
            FileUtil.saveResource("lang/zh.json", zhLangFile);
        }

        if (!enLangFile.exists()) {
            FileUtil.saveResource("lang/en.json", enLangFile);
        }

        if (!langs.contains(String.valueOf(LclConfig.instance.lclConfigMap.get("lang"))))
            LclConfig.instance.lclConfigMap.put("lang", "zh");
        var objectMapper = new ObjectMapper();
        var configJson = objectMapper.readTree(new File(FileUtil.getBaseDir(), "/LcLConfig/lang/" + String.valueOf(LclConfig.instance.lclConfigMap.get("lang") + ".json")));
        var iterator = configJson.fieldNames();
        String key;
        String value;
        while (iterator.hasNext()) {
            key = iterator.next();
            value = configJson.get(key).asText();
            lclLocaleMap.put(key, value);
        }
    }
}
