package me.logincraftlaunch.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import me.logincraftlaunch.apis.LclConfig;
import me.logincraftlaunch.apis.LclSkin;
import me.logincraftlaunch.main.Main;
import me.logincraftlaunch.ui.Container;
import me.logincraftlaunch.ui.InterfaceManager;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class FrameController implements Initializable {

    public static FrameController instance;

    public static Map<String, Node> panes = new HashMap<>();

    public static Node getInstance(String name) {
        return panes.get(name);
    }

    public VBox vertical;
    public Pane pane, base;
    public Pane title, hover;
    public AnchorPane apane;
    public ImageView avatar, mainBackground, logo;
    public Text titleText;
    public Label username;
    public Line l1, l2, l3, l4, l5, l6;
    public Rectangle rec, rec1;

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        avatar.setImage(new Image(Main.class.getResourceAsStream("/css/images/LCL.png")));
        logo.setImage(new Image(Main.class.getResourceAsStream("/css/images/LCL.png")));
        username.setText((String) LclConfig.instance.lclConfigMap.get("name"));
        InterfaceManager.containers.forEach(container -> vertical.getChildren().add(container.getButton()));
        InterfaceManager.containers.forEach(container -> {
            container.getButton().setOnMouseClicked(event -> {
                pane.getChildren().clear();
                pane.getChildren().add(container.getPane());
            });
        });

        title.setOnMousePressed(event -> {
            event.consume();
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        title.setOnMouseDragged(event -> {
            event.consume();
            Main.mainGui.getStage().setX(event.getScreenX() - xOffset);
            if (event.getScreenY() - yOffset < 0) {
                Main.mainGui.getStage().setY(0);
            } else {
                Main.mainGui.getStage().setY(event.getScreenY() - yOffset);
            }
        });
    }

    public static void load(String fxml, String buttonName, Image background) {
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
            InterfaceManager.addInterface(Container.create(fxml, button));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load(Parent parent, String buttonName, Image background) {
        panes.put(buttonName, parent);
        FrameController.instance.apane.setPrefHeight(panes.size() * 45 + 100);
        Button button;
        if (background != null) {
            ImageView imageView = new ImageView(background);
            imageView.setFitHeight(30);
            imageView.setFitWidth(30);
            button = new Button(buttonName, imageView);
        } else button = new Button(buttonName);
        button.setAlignment(Pos.BASELINE_LEFT);
        button.setPrefSize(200, 45);
        button.setStyle("-fx-border-style: solid;" +
                "-fx-background-color: rgba(255,255,255,0.7);" +
                "-fx-font-size:16;" +
                "-fx-border-width: 0px 0px 0px 10px;" +
                "-fx-border-color: rgba(0, 0, 0, 0) rgba(0, 0, 0, 0) rgba(0, 0, 0, 0) rgba(0, 0, 0, 0);" +
                "-fx-text-fill: rgba(86,86,86,1);"
        );
        InterfaceManager.addInterface(Container.create(buttonName, button));
    }


    /**
     * 载入皮肤
     *
     * @param lclSkin
     */
    public static void loadSkin(LclSkin lclSkin) {
        FrameController.instance.l1.setStroke(Color.rgb(lclSkin.getAwtLineColor().getRed(), lclSkin.getAwtLineColor().getGreen(), lclSkin.getAwtLineColor().getBlue()));
        FrameController.instance.l2.setStroke(Color.rgb(lclSkin.getAwtLineColor().getRed(), lclSkin.getAwtLineColor().getGreen(), lclSkin.getAwtLineColor().getBlue()));
        FrameController.instance.l3.setStroke(Color.rgb(lclSkin.getAwtLineColor().getRed(), lclSkin.getAwtLineColor().getGreen(), lclSkin.getAwtLineColor().getBlue()));
        FrameController.instance.l4.setStroke(Color.rgb(lclSkin.getAwtLineColor().getRed(), lclSkin.getAwtLineColor().getGreen(), lclSkin.getAwtLineColor().getBlue()));
        FrameController.instance.l5.setStroke(Color.rgb(lclSkin.getAwtLineColor().getRed(), lclSkin.getAwtLineColor().getGreen(), lclSkin.getAwtLineColor().getBlue()));
        FrameController.instance.l6.setStroke(Color.rgb(lclSkin.getAwtLineColor().getRed(), lclSkin.getAwtLineColor().getGreen(), lclSkin.getAwtLineColor().getBlue()));
        FrameController.instance.l1.setOpacity(lclSkin.getOpacity());
        FrameController.instance.l2.setOpacity(lclSkin.getOpacity());
        FrameController.instance.l3.setOpacity(lclSkin.getOpacity());
        FrameController.instance.l4.setOpacity(lclSkin.getOpacity());
        FrameController.instance.l5.setOpacity(lclSkin.getOpacity());
        FrameController.instance.l6.setOpacity(lclSkin.getOpacity());
        FrameController.instance.rec.setFill(Color.rgb(lclSkin.getAwtMainColor().getRed(), lclSkin.getAwtMainColor().getGreen(), lclSkin.getAwtMainColor().getBlue()));
        FrameController.instance.rec1.setFill(Color.rgb(lclSkin.getAwtMainColor().getRed(), lclSkin.getAwtMainColor().getGreen(), lclSkin.getAwtMainColor().getBlue()));
        FrameController.instance.rec.setOpacity(lclSkin.getOpacity());
        FrameController.instance.rec1.setOpacity(lclSkin.getOpacity());
        FrameController.instance.username.setStyle("-fx-text-fill: rgba(" + lclSkin.getAwtTitleColor().getRed() + "," + lclSkin.getAwtTitleColor().getGreen() + "," + lclSkin.getAwtTitleColor().getBlue() + "," + "1);");
        FrameController.instance.titleText.setFill(Color.rgb(lclSkin.getAwtTitleColor().getRed(), lclSkin.getAwtTitleColor().getGreen(), lclSkin.getAwtTitleColor().getBlue()));
        FrameController.instance.mainBackground.setImage(lclSkin.getBackground());
    }

    @FXML
    void onExit() {
        LclConfig.instance.save();
        System.exit(0);
    }
}
