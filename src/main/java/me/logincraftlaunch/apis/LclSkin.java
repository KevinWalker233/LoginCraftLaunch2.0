package me.logincraftlaunch.apis;

import javafx.scene.image.Image;
import me.logincraftlaunch.utils.ColorTranslated;
import me.logincraftlaunch.utils.FileUtil;
import net.lingala.zip4j.io.ZipInputStream;
import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.util.Objects;

public class LclSkin {

    public static LclSkin instance;

    private java.awt.Color awtMainColor = null;
    private java.awt.Color awtTitleColor = null;
    private java.awt.Color awtLineColor = null;
    private double opacity = 1;
    private Image background = null;


    public LclSkin() {
        LclSkin.instance = this;
        load();
    }

    /**
     * 加载/重载 启动器皮肤
     */
    public void load() {

        File folder = new File(FileUtil.getBaseDir(), "/LcLConfig/skin");
        if (!folder.exists()) folder.mkdir();
        if (folder.listFiles() == null || Objects.requireNonNull(folder.listFiles()).length == 0) {
            FileUtil.saveResource("default.zip", new File(folder, "default.zip"));
        }

        try (ZipInputStream skinInputStream = FileUtil.getInputStream(new File(FileUtil.getBaseDir(), String.valueOf(LclConfig.instance.lclConfigMap.get("skin"))), "skin.json")) {
            assert skinInputStream != null;
            Reader reader = new InputStreamReader(skinInputStream, "utf-8");
            JSONObject skin = new JSONObject(readerToString((InputStreamReader) reader).trim());
            awtMainColor = ColorTranslated.toColorFromString(skin.getString("colorMain"));
            awtLineColor = ColorTranslated.toColorFromString(skin.getString("colorLine"));
            awtTitleColor = ColorTranslated.toColorFromString(skin.getString("colorTitle"));
            opacity = skin.getDouble("opacity");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try (ZipInputStream skinInputStream = FileUtil.getInputStream(new File(FileUtil.getBaseDir(), String.valueOf(LclConfig.instance.lclConfigMap.get("skin"))), "Background.png")) {
            assert skinInputStream != null;
            background = new Image(skinInputStream);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Image getBackground() {
        return background;
    }

    public Color getAwtMainColor() {
        return awtMainColor;
    }

    public Color getAwtTitleColor() {
        return awtTitleColor;
    }

    public Color getAwtLineColor() {
        return awtLineColor;
    }

    public double getOpacity() {
        return opacity;
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
        }
        else if (buffer.toString().endsWith("\uFEFF")) {
            value = buffer.toString().replace("\uFEFF", "");
        }
        reader.close();
        return value;
    }
}
