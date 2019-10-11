package sample.utility;

import animatefx.animation.ZoomIn;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.inteface.DispatcherController;

import java.io.IOException;

/**
 * Shoh Jahon tomonidan 7/26/2019 da qo'shilgan.
 */
public class ControllerUtil<T>{
    private DispatcherController controller;
    private int screenW, screenH;
    private Parent root;
    private String viewUri;
    private ObservableList<T> list;
    private Object dto;
    private int index;

    public ControllerUtil(String viewUri, int screenW, int screenH,
                          ObservableList<T> list, Object dto, int index){
        this.viewUri = viewUri;
        this.screenW = screenW;
        this.screenH = screenH;
        this.list = list;
        this.dto = dto;
        this.index = index;

    }

    public void showWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(viewUri));
        root = loader.load();
        controller = loader.getController();
        Scene scene = new Scene(root,screenW,screenH);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        StageStyle style = StageStyle.TRANSPARENT;
        stage.initStyle(style);
        controller.setData(list,dto,index);
        controller.setStage(stage);
        stage.show();
    }
}
