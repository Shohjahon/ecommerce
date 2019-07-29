package sample.inteface;

import javafx.collections.ObservableList;
import javafx.stage.Stage;
import sample.controller.CreateProductTypeController;

/**
 * Shoh Jahon tomonidan 6/28/2019 da qo'shilgan.
 */
public interface DispatcherController<T> {

    void setStage(Stage stage);

    void setData(ObservableList<T> list, T dto, int index);
}
