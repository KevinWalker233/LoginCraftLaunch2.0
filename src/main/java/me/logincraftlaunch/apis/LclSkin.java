package me.logincraftlaunch.apis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.var;
import me.logincraftlaunch.utils.ColorTranslated;
import me.logincraftlaunch.utils.FileUtil;

import java.awt.*;
import java.io.*;
import java.util.Objects;

public class LclSkin {

    public static LclSkin instance;

    @Getter private java.awt.Color awtMainColor = null;
    @Getter private java.awt.Color awtTitleColor = null;
    @Getter private java.awt.Color awtLineColor = null;
    @Getter private double opacity = 1;
    @Getter private Image background = null;


    public LclSkin() {
        LclSkin.instance = this;
        load();
    }

    /**
     * 加载/重载 启动器皮肤
     */
    public void load() {

        var folder = new File(FileUtil.getBaseDir(), "/LcLConfig/skin");
        if (!folder.exists()) folder.mkdir();
        if (folder.listFiles() == null || Objects.requireNonNull(folder.listFiles()).length == 0) {
            FileUtil.saveResource("default.zip", new File(folder, "default.zip"));
        }

        try (InputStream skinInputStream = FileUtil.getInputStream(new File(FileUtil.getBaseDir(), String.valueOf(LclConfig.instance.lclConfigMap.get("skin"))), "skin.json")) {
            assert skinInputStream != null;
            var reader = new InputStreamReader(skinInputStream, "utf-8");
            var objectMapper = new ObjectMapper();
            var skin = objectMapper.readTree(readerToString((InputStreamReader) reader).trim());
            awtMainColor = ColorTranslated.toColorFromString(skin.get("colorMain").asText());
            awtLineColor = ColorTranslated.toColorFromString(skin.get("colorLine").asText());
            awtTitleColor = ColorTranslated.toColorFromString(skin.get("colorTitle").asText());
            opacity = skin.get("opacity").asDouble();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (InputStream skinInputStream = FileUtil.getInputStream(new File(FileUtil.getBaseDir(), String.valueOf(LclConfig.instance.lclConfigMap.get("skin"))), "Background.png")) {
            assert skinInputStream != null;
            background = new Image(skinInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String readerToString(InputStreamReader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer();
        String line = " ";
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        String value = buffer.toString();
        if (buffer.toString().startsWith("\uFEFF")) {
            value = buffer.toString().replace("\uFEFF", "");
        } else if (buffer.toString().endsWith("\uFEFF")) {
            value = buffer.toString().replace("\uFEFF", "");
        }
        reader.close();
        return value;
    }
}
