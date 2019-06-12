package me.logincraftlaunch.apis;

import me.logincraftlaunch.utils.FileUtil;
import org.json.JSONObject;

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
    public void load() {
        File langFile = new File(FileUtil.getBaseDir(), "/LcLConfig/lang");
        File zhLangFile = new File(FileUtil.getBaseDir(), "/LcLConfig/lang/zh.json");
        File enLangFile = new File(FileUtil.getBaseDir(), "/LcLConfig/lang/en.json");

        if (!langFile.exists()) langFile.mkdir();

        if (!zhLangFile.exists()) {
            FileUtil.saveResource("lang/zh.json", zhLangFile);
        }

        if (!enLangFile.exists()) {
            FileUtil.saveResource("lang/en.json", enLangFile);
        }

        if (!langs.contains(String.valueOf(LclConfig.instance.lclConfigMap.get("lang"))))
            LclConfig.instance.lclConfigMap.put("lang", "zh");
        JSONObject configJson = new JSONObject(Objects.requireNonNull(FileUtil.toString(new File(FileUtil.getBaseDir(), "/LcLConfig/lang/" + String.valueOf(LclConfig.instance.lclConfigMap.get("lang")) + ".json"), "utf-8")));
        Iterator<?> iterator = configJson.keys();
        String key;
        String value;
        while (iterator.hasNext()) {
            key = (String) iterator.next();
            value = configJson.getString(key);
            lclLocaleMap.put(key, value);
        }
    }


    public static void main(String[] args) {
        //载入配置文件
        LclConfig lclConfig = new LclConfig();
        //载入语言文件
        LclLocale lclLocale = new LclLocale();
        //测试输出语言文件
        for (String value : lclLocale.lclLocaleMap.keySet()) {
            System.out.println("内容：" + value + " 值：" + lclLocale.lclLocaleMap.get(value));
        }

        //修改已经存在的配置文件
        LclConfig.instance.lclConfigMap.put("lang", "en");
        //重载更改配置后的语言文件
        lclLocale.load();
        //测试输出语言文件
        for (String value : lclLocale.lclLocaleMap.keySet()) {
            System.out.println("内容：" + value + " 值：" + lclLocale.lclLocaleMap.get(value));
        }

        //保存更改后的配置文件
        lclConfig.save();
    }
}
