package sample.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sample.dao.ProductTypeDao;
import sample.inteface.DispatcherController;
import sample.model.ProductType;
import sample.utility.AlertUtil;
import sample.utility.DatabaseUtil;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Shoh Jahon tomonidan 7/23/2019 da qo'shilgan.
 */
public class UpdateProductTypeController implements Initializable,DispatcherController<ProductType> {
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
    private ProductTypeController productTypeController;
    private static UpdateProductTypeController INSTANCE;
    private ObservableList<ProductType> list;
    private ProductType productType;
    private int index;

    public UpdateProductTypeController(){
        INSTANCE = this;
    }

    public void setData(ObservableList<ProductType> list,ProductType productType, int index) {
        this.list = list;
        this.productType= productType;
        productTypeField.setText(productType.getProductType());
        this.index = index;
    }

    @Override
    public void setFilterField(JFXTextField filterField) {

    }

    @Override
    public void setMainBtns(JFXButton export, JFXButton deleteBtn,JFXButton brief_btn) {

    }

    public static UpdateProductTypeController getInstance(){
        return INSTANCE;
    }


    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
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
    public void handleUpdate(){
        if (!(productTypeField.getText().isEmpty() && productTypeField.getText() == null)){
            productType.setProductType(productTypeField.getText());
            try {
                productTypeDao.updateProductType(productType);
                list.set(index,productType);
                handleCancel();
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtil.showAlert(Alert.AlertType.ERROR,
                        "Хатолик",
                        "Хатолик юз берди! ",
                        "Дастур билан боғлиқ хатолик юз берди!  \n" +
                                "Илтимос дастур администратори билан боғланинг! ");
            }
        }else {
            AlertUtil.showAlert(Alert.AlertType.WARNING,
                    "Диққат!",
                    "Илтимос киритилган маълумотни текширинг!",
                    "Илтимос!  Киритилган маълумот бўш сатр эмаслигини\n" +
                            "ва уни тўғри киритилганлигига текширинг.");
        }
    }

    @FXML
    public void handleCancel(){
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productTypeDao = DatabaseUtil.getProductTypeDao();

    }
}
