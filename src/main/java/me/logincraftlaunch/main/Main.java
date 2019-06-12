package me.logincraftlaunch.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.logincraftlaunch.apis.LclConfig;
import me.logincraftlaunch.apis.LclLocale;
import me.logincraftlaunch.apis.LclSkin;
import me.logincraftlaunch.ui.Container;
import me.logincraftlaunch.ui.InterfaceManager;
import me.logincraftlaunch.ui.controller.FrameController;
import me.logincraftlaunch.utils.FileUtil;
import me.logincraftlaunch.utils.GuiBase;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    public static GuiBase mainGui;

    @Override
    public void start(Stage primaryStage) throws Exception {
        new LclConfig();
        new LclLocale();
        new LclSkin();

        GuiBase mainGui = new GuiBase("Frame", primaryStage);
        Main.mainGui = mainGui;
        mainGui.getScene().setFill(Color.TRANSPARENT);
        mainGui.getStage().setTitle("LoginCraftLaunch2.0-Demo");
        mainGui.getStage().getIcons().add(new Image(Main.class.getResourceAsStream("/css/images/LCL.png")));
        mainGui.getStage().initStyle(StageStyle.TRANSPARENT);
        mainGui.getStage().setResizable(true);
        mainGui.getStage().setOnCloseRequest((e) ->
        {
            LclConfig.instance.save();
            System.exit(0);
        });
        mainGui.getStage().setScene(mainGui.getScene());
        mainGui.getStage().show();

        FrameController.loadSkin(LclSkin.instance);
        load("MainPage", LclLocale.instance.lclLocaleMap.get("MainPage"), new Image(Main.class.getResourceAsStream("/css/images/MainPage.png")), true);
//        load("Settings", LclLocale.instance.lclLocaleMap.get("Settings"), new Image(Main.class.getResourceAsStream("/css/images/Settings.png")),false);
        FrameController.load("Settings", LclLocale.instance.lclLocaleMap.get("Settings"), new Image(Main.class.getResourceAsStream("/css/images/Settings.png")));
        new LoadPlugins();
//        setupLogger();
    }


    private static void load(String fxml, String buttonName, Image background, boolean showDefault) {
        Parent parent;
        try {
            parent = FXMLLoader.load(Main.class.getResource("/fxml/" + fxml + ".fxml"));
            FrameController.panes.put(fxml, parent);
            FrameController.instance.apane.setPrefHeight(FrameController.panes.size() * 45 + 100);
            ImageView imageView = new ImageView(background);
            imageView.setFitHeight(30);
            imageView.setFitWidth(30);
            Button button = new Button(buttonName, imageView);
            button.setAlignment(Pos.BASELINE_LEFT);
            button.setPrefSize(200, 45);
            button.setStyle("-fx-border-style: solid;" +
                    "-fx-background-color: rgba(255,255,255,0.7);" +
                    "-fx-font-size:16;" +
                    "-fx-border-width: 0px 0px 0px 10px;" +
                    "-fx-border-color: rgba(0, 0, 0, 0) rgba(0, 0, 0, 0) rgba(0, 0, 0, 0) rgba(0, 0, 0, 0);" +
                    "-fx-text-fill: rgba(86,86,86,1);"
            );

            if (showDefault) {
                button.setStyle("-fx-border-style: solid;" +
                        "-fx-background-color: rgba(255,255,255,0.7);" +
                        "-fx-font-size:16;" +
                        "-fx-border-width: 0px 0px 0px 10px;" +
                        "-fx-border-color: rgba(0, 0, 0, 0) rgba(0, 0, 0, 0) rgba(0, 0, 0, 0) rgba(86,86,86);" +
                        "-fx-text-fill: rgba(86,86,86,1);"
                );
            }

            InterfaceManager.addInterface(Container.create(fxml, button));

            if (showDefault)
                FrameController.instance.pane.getChildren().add(FrameController.getInstance(fxml));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static void setupLogger() {
        try {
            if (!new File(FileUtil.getBaseDir(), "/LclConfig").exists())
                new File(FileUtil.getBaseDir(), "/LclConfig").mkdir();
            if (new File(FileUtil.getBaseDir(), "LclConfig/log.txt").exists())
                new File(FileUtil.getBaseDir(), "LclConfig/log.txt").delete();
            new File(FileUtil.getBaseDir(), "LclConfig/log.txt").createNewFile();
            System.setOut(new PrintStream(new File(FileUtil.getBaseDir(), "LclConfig/log.txt")));
            System.setErr(new PrintStream(new File(FileUtil.getBaseDir(), "LclConfig/log.txt")));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
