package sample;

import animatefx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import sample.controller.Controller;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class Main extends Application {

    private boolean firstTime;
    private TrayIcon trayIcon;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/index.fxml"));
        Parent root = loader.load();
        final Controller controller = loader.getController();
        Scene scene = new Scene(root,1300,800);
        StageStyle style = StageStyle.TRANSPARENT;
        primaryStage.setScene(scene);
        primaryStage.initStyle(style);
        controller.setStage(primaryStage,scene);

        primaryStage.show();
        new FadeIn(root).play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
