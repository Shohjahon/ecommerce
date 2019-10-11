package sample.controller;

import animatefx.animation.FadeOut;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sample.dao.ProductTypeDao;
import sample.inteface.DispatcherController;
import sample.model.ProductType;
import sample.utility.AlertUtil;
import sample.utility.DatabaseUtil;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Shoh Jahon tomonidan 6/28/2019 da qo'shilgan.
 */
public class CreateProductTypeController implements Initializable,DispatcherController<ProductType> {
    private Stage stage;
    private double dragAnchorY;
    private double dragAnchorX;
    @FXML
    private JFXButton addBtn;
    @FXML
    private JFXButton cancelBtn;
    @FXML
    private JFXTextField productTypeField;
    private ProductTypeDao productTypeDao;
    private ObservableList<ProductType> productTypes;
    private TableView<ProductType> product_type_table;
    private static CreateProductTypeController INSTANCE;
    private ProductTypeController productTypeController;

    public CreateProductTypeController(){
        INSTANCE = this;
    }

    public static CreateProductTypeController getInstance(){
        return INSTANCE;
    }


    @Override
    public void setStage(Stage stage){
        this.stage = stage;
    }

    @Override
    public void setData(ObservableList<ProductType> list, ProductType dto, int index) {

    }

    @Override
    public void setFilterField(JFXTextField filterField) {

    }

    @Override
    public void setMainBtns(JFXButton export, JFXButton deleteBtn,JFXButton brief_btn) {

    }


    public void setProductTypes(ObservableList<ProductType> productTypes) {
        this.productTypes = productTypes;
    }


    @FXML
    public void onMousePressed(MouseEvent me) {
        dragAnchorY = me.getScreenY() - stage.getY();
        dragAnchorX = me.getScreenX() - stage.getX();
    }
    @FXML
    public void onMouseDragged(MouseEvent me) {
        stage.setX(me.getScreenX() - dragAnchorX);
        stage.setY(me.getScreenY() - dragAnchorY);
    }

    @FXML
    public void handleCancel(){
        stage.close();

    }

    @FXML
    public void handleCreate(){
        String productType = productTypeField.getText();
        if (productType.isEmpty() || productType == null){
            AlertUtil.showAlert(Alert.AlertType.WARNING,
                    "Диққат!",
                    "Илтимос киритилган маълумотни текширинг!",
                    "Илтимос!  Киритилган маълумот бўш сатр эмаслигини\n" +
                            "ва уни тўғри киритилганлигига текширинг.");
        }else {
            ProductType productT = new ProductType(productType);
            try {
                productTypeDao.insetProductType(productT);
                productTypeController = ProductTypeController.getInstance();
                productTypeController.populateTableView();
                handleCancel();
                productTypeController.refreshFilter();
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtil.showAlert(Alert.AlertType.ERROR,
                        "Хатолик",
                        "Хатолик юз берди! ",
                        "Дастур билан боғлиқ хатолик юз берди!  \n" +
                                "Илтимос дастур администратори билан боғланинг! ");

            }
        }
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productTypeDao = DatabaseUtil.getProductTypeDao();
    }
}
