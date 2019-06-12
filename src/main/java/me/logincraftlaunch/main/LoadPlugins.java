package me.logincraftlaunch.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.logincraftlaunch.apis.LclPlugin;
import me.logincraftlaunch.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class LoadPlugins {

    public LoadPlugins() {
        File folder = new File(FileUtil.getBaseDir(), "/LcLConfig/plugins");
        if (!folder.exists()) folder.mkdir();
        try {
            List<String> pluginsList = FileUtil.getFileList(new File(FileUtil.getBaseDir(), "/LcLConfig/plugins/"), "jar");
            if(pluginsList.size() != 0) {
                for (String value : pluginsList) {
                    File file = new File(FileUtil.getBaseDir(), "/LcLConfig/plugins/" + value + ".jar");
                    URL url = new URL("jar:" + file.toURI() + "!/plugin.lcl");
                    try (InputStream is = url.openStream()) {
                        byte b[] = new byte[1000];
                        try {
                            is.read(b);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode configJson = objectMapper.readTree(new String(b).trim());
                        LclPlugin lclPlugin = getInstance(value, configJson.get("mainClass").asText());
                        lclPlugin.loadPlugin();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public LclPlugin getInstance(String pluginName, String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        try {
            File file = new File(FileUtil.getBaseDir(), "/LcLConfig/plugins/" + pluginName + ".jar");
            if (!file.exists()) file.createNewFile();
            URL[] urls = new URL[1];
            urls[0] = file.toURL();
            URLClassLoader urlClassLoader = new URLClassLoader(urls);
            Class<?> clazz = urlClassLoader.loadClass(className);
            Object instance = clazz.newInstance();
            return (LclPlugin) instance;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
