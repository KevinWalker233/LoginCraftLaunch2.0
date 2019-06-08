package me.logincraftlaunch.ui.controller;

import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;

import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {

    public static MainPageController instance;

    public SplitMenuButton test;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
        test.setText("Shutdown");
        MenuItem menuItem = new MenuItem("test");
        test.getItems().addAll(menuItem);
        menuItem.setOnAction(e -> {
            test.setText(menuItem.getText());
        });
        test.setOnAction(e -> {
            System.out.println(test.getText());
        });
    }
}
