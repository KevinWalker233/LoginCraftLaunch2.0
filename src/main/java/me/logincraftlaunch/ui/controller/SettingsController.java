package me.logincraftlaunch.ui.controller;

import com.sun.management.OperatingSystemMXBean;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import me.logincraftlaunch.apis.LclConfig;
import me.logincraftlaunch.apis.LclLocale;
import me.logincraftlaunch.main.Main;
import me.logincraftlaunch.utils.FileUtil;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class SettingsController implements Initializable {

    public static SettingsController instance;
    private static ExecutorService service = Executors.newFixedThreadPool(1);

    public VBox settingPane;
    public Text Setting;
    public Text GameSettings, LaunchSetting, NetworkSetting;
    public Text ProxyHost, ProxyPort, ProxyUserName, ProxyPassword, PhysicalMemory;
    public Label PlayerName, Memory, JavaPath, GamePath, Language, ProxyMode;
    public TextField name, maxMemory;
    public TextField gamePath, javaPath;
    public TextField proxyHost, proxyPort, proxyUser, proxyPassword;
    public ComboBox comboBoxLanguage, comboBoxProxy;
    public Text VersionInfo;
    public Button CheckUpdate, Connect, LanguageButton;
    public Button ChooeFile, ChooeFile1;
    public ImageView setting;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        setLanguage(LclLocale.instance);
        loadSettings(LclConfig.instance, LclLocale.instance);
        setting.setImage(new Image(Main.class.getResourceAsStream("/css/images/Settings.png")));
    }

    public void loadSettings(LclConfig lclConfig, LclLocale lclLocale) {
        javaPath.setText(String.valueOf(lclConfig.lclConfigMap.get("javaPath")));
        gamePath.setText(String.valueOf(lclConfig.lclConfigMap.get("gamePath")));
        name.setText(String.valueOf(lclConfig.lclConfigMap.get("name")));
        maxMemory.setText(String.valueOf(lclConfig.lclConfigMap.get("maxMemory")));
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
        PhysicalMemory.setText(lclLocale.lclLocaleMap.get("PhysicalMemory") + ":" + String.valueOf(osmxb.getTotalPhysicalMemorySize() / (1024 * 1024)) + "MB");
        List<String> langList = FileUtil.getFileList(new File(FileUtil.getBaseDir(), "/LcLConfig/lang"), "json");
        comboBoxLanguage.getItems().setAll(langList);

        comboBoxLanguage.setValue(String.valueOf(lclConfig.lclConfigMap.get("lang")));

        comboBoxProxy.getItems().addAll("Http", "Socks");

        if (String.valueOf(lclConfig.lclConfigMap.get("proxyMode")) != null) {
            comboBoxProxy.setValue(String.valueOf(lclConfig.lclConfigMap.get("proxyMode")));
        }
        proxyHost.setText(String.valueOf(lclConfig.lclConfigMap.get("proxyHost")));
        proxyPort.setText(String.valueOf(lclConfig.lclConfigMap.get("proxyPort")));
        proxyUser.setText(String.valueOf(lclConfig.lclConfigMap.get("proxyUser")));


        ChooeFile1.setOnAction(oa -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(lclLocale.lclLocaleMap.get("JavaPath"));
            fileChooser.setInitialFileName("javaw");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("javaw", "javaw.exe")
            );
            File file = fileChooser.showOpenDialog(Main.mainGui.getStage());
            javaPath.setText(file.getPath());
            lclConfig.lclConfigMap.put("javaPath", file.getPath());
            lclConfig.save();
        });

        ChooeFile.setOnAction(oa -> {
            DirectoryChooser folderChooser = new DirectoryChooser();
            folderChooser.setTitle(lclLocale.lclLocaleMap.get("GamePath"));
            folderChooser.setInitialDirectory(FileUtil.getBaseDir());
            File selectedFile = folderChooser.showDialog(Main.mainGui.getStage());
            gamePath.setText(selectedFile.getPath());
            lclConfig.lclConfigMap.put("gamePath", selectedFile.getPath());
            lclConfig.save();
        });

        LanguageButton.setOnMouseClicked((MouseEvent event) -> {
            lclConfig.lclConfigMap.put("lang", comboBoxLanguage.getValue());
            lclConfig.save();
        });

        Connect.setOnMouseClicked((MouseEvent event) -> {
            if (comboBoxLanguage.getValue().equals("Http")) {
                Properties prop = System.getProperties();
                prop.setProperty("http.proxyHost", proxyHost.getText());
                prop.setProperty("http.proxyPort", proxyPort.getText());
            }
            else if (comboBoxLanguage.getValue().equals("Socks")) {
                Properties prop = System.getProperties();
                prop.setProperty("socksProxyHost", proxyHost.getText());
                prop.setProperty("socksProxyPort", proxyPort.getText());
            }
            Authenticator.setDefault(new MyAuthenticator(proxyUser.getText(), proxyPassword.getText()));
            lclConfig.lclConfigMap.put("proxyHost", proxyHost.getText());
            lclConfig.lclConfigMap.put("proxyPort", proxyPort.getText());
            lclConfig.lclConfigMap.put("proxyUser", proxyUser.getText());
            lclConfig.save();
        });

        name.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (name.getText() != null && !name.getText().equals("") && Pattern.compile("^[A-Za-z][A-Za-z1-9_-]+$").matcher(name.getText()).matches()) {
                    lclConfig.lclConfigMap.put("name", name.getText());
                    FrameController.instance.username.setText(name.getText());
                    lclConfig.save();
                }
                else {
                    name.setText("Administrator");
                }
            }
        });

        maxMemory.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (maxMemory.getText() != null && !maxMemory.getText().equals("") && Pattern.compile("^[0-9]*$").matcher(maxMemory.getText()).matches()) {
                    lclConfig.lclConfigMap.put("maxMemory", maxMemory.getText());
                    lclConfig.save();
                }
                else {
                    maxMemory.setText("1024");
                }
            }
        });
    }

    public void setLanguage(LclLocale lclLocale) {
        Setting.setText(lclLocale.lclLocaleMap.get(Setting.getText()));
        GameSettings.setText(lclLocale.lclLocaleMap.get(GameSettings.getText()));
        PlayerName.setText(lclLocale.lclLocaleMap.get(PlayerName.getText()));
        Memory.setText(lclLocale.lclLocaleMap.get(Memory.getText()));
        GamePath.setText(lclLocale.lclLocaleMap.get(GamePath.getText()));
        JavaPath.setText(lclLocale.lclLocaleMap.get(JavaPath.getText()));
        LaunchSetting.setText(lclLocale.lclLocaleMap.get(LaunchSetting.getText()));
        Language.setText(lclLocale.lclLocaleMap.get(Language.getText()));
        NetworkSetting.setText(lclLocale.lclLocaleMap.get(NetworkSetting.getText()));
        ProxyMode.setText(lclLocale.lclLocaleMap.get(ProxyMode.getText()));
        ProxyHost.setText(lclLocale.lclLocaleMap.get(ProxyHost.getText()));
        ProxyPort.setText(lclLocale.lclLocaleMap.get(ProxyPort.getText()));
        ProxyUserName.setText(lclLocale.lclLocaleMap.get(ProxyUserName.getText()));
        ProxyPassword.setText(lclLocale.lclLocaleMap.get(ProxyPassword.getText()));
        ChooeFile.setText(lclLocale.lclLocaleMap.get("ChooeFile"));
        ChooeFile1.setText(lclLocale.lclLocaleMap.get("ChooeFile"));
        Connect.setText(lclLocale.lclLocaleMap.get(Connect.getText()));
        CheckUpdate.setText(lclLocale.lclLocaleMap.get("CheckUpdate"));
    }

    static class MyAuthenticator extends Authenticator {
        private String user = "";
        private String password = "";

        public MyAuthenticator(String user, String password) {
            this.user = user;
            this.password = password;
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(user, password.toCharArray());
        }
    }
}
