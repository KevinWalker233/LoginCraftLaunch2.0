package me.logincraftlaunch.ui;

import javafx.scene.Node;
import me.logincraftlaunch.ui.controller.FrameController;

import java.util.ArrayList;
import java.util.List;

public class InterfaceManager {

    public static List<Container> containers = new ArrayList<>();

    public static boolean blocked = false;

    public static void addInterface(Container container) {
        FrameController.instance.vertical.getChildren().add(container.getButton());
        container.getButton().setOnMouseClicked(event -> {
            Node current = FrameController.instance.pane.getChildren().get(0);
            if (current.equals(container.getPane()) || FrameController.instance.pane.getChildren().contains(container.getPane()) || blocked)
                return;
            blocked = true;
            FrameController.instance.pane.getChildren().add(container.getPane());
            InterfaceManager.containers.forEach(obj -> {
                obj.getButton().setStyle("-fx-border-style: solid;" +
                        "-fx-background-color: rgba(255,255,255,0.7);" +
                        "-fx-font-size:16;" +
                        "-fx-border-width: 0px 0px 0px 10px;" +
                        "-fx-border-color: rgba(0, 0, 0, 0) rgba(0, 0, 0, 0) rgba(0, 0, 0, 0) rgba(0, 0, 0, 0);" +
                        "-fx-text-fill: rgba(86,86,86,1);"
                );
            });
            container.getButton().setStyle("-fx-border-style: solid;" +
                    "-fx-background-color: rgba(255,255,255,0.7);" +
                    "-fx-font-size:16;" +
                    "-fx-border-width: 0px 0px 0px 10px;" +
                    "-fx-border-color: rgba(0, 0, 0, 0) rgba(0, 0, 0, 0) rgba(0, 0, 0, 0) rgba(86,86,86);" +
                    "-fx-text-fill: rgba(86,86,86,1);"
            );
            Transition.playRandomPair(container.getPane(), event1 -> {
                    },
                    current, event1 -> {
                        FrameController.instance.pane.getChildren().remove(current);
                        blocked = false;
                    });
        });
        containers.add(container);
    }

}
