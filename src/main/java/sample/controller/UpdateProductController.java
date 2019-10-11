package sample.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sample.dao.ProductDao;
import sample.dao.ProductTypeDao;
import sample.inteface.DispatcherController;
import sample.listener.ComboBoxAutoComplete;
import sample.model.Product;
import sample.model.ProductType;
import sample.model.dto.ProductDto;
import sample.utility.AlertUtil;
import sample.utility.DatabaseUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Shoh Jahon tomonidan 7/23/2019 da qo'shilgan.
 */
public class UpdateProductController implements Initializable,DispatcherController<ProductDto>{
    private Stage stage;
    @FXML
    private JFXButton cancelBtn;
    @FXML
    private JFXButton addBtn;
    private double dragAnchorY;
    private double dragAnchorX;
    private ProductDao productDao;
    private ProductTypeDao productTypeDao;
    private ObservableList<ProductType> productTypeList;
    private ObservableList<ProductDto> productDtos;
    private int index;
    private ProductDto productDto;
    @FXML
    private JFXTextField productNameField;
    @FXML
    private JFXComboBox productTypesBox;
    @FXML
    private JFXTextArea productDescriptionField;


    public void setData(ObservableList<ProductDto> productDtos,ProductDto product, int index){
        this.productDtos = productDtos;
        this.productDto = product;
        this.index = index;

        try {
            Product p = productDao.findProductById(product.getId());
            productTypesBox.getSelectionModel().select(p.getProductType());
            productNameField.setText(product.getProductName());
            productDescriptionField.setText(product.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showAlert(Alert.AlertType.ERROR,
                    "Хатолик",
                    "Хатолик юз берди! ",
                    "Дастур билан боғлиқ хатолик юз берди!  \n" +
                            "Илтимос дастур администратори билан боғланинг! ");
        }
    }

    @Override
    public void setFilterField(JFXTextField filterField) {

    }

    @Override
    public void setMainBtns(JFXButton export, JFXButton deleteBtn,JFXButton brief_btn) {

    }


    @FXML
    public void handleUpdateProduct(){
        String pName = productNameField.getText();
        String des = productDescriptionField.getText();

        ProductType productType = (ProductType) productTypesBox.getValue();

        boolean isValid = (pName.isEmpty() || productType == null);

        if (!isValid){
            Product product = new Product(pName);
            product.setDescription(des);
            product.setProductType(productType);
            product.setId(productDto.getId());
            try {
                productDao.updateProduct(product);
                productDtos.set(index,ProductDto.mapToProductDto(product));
                handleCancel();
            }catch (Exception ex){
                ex.printStackTrace();
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productTypeList = FXCollections.observableArrayList();

        productDao = DatabaseUtil.getProductDao();
        productTypeDao = DatabaseUtil.getProductTypeDao();

        try {
            productTypeList.addAll(productTypeDao.findAllProductTypes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        productTypesBox.setItems(productTypeList);

        new ComboBoxAutoComplete<String>(productTypesBox);
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void handleCancel(){
        stage.close();

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

}
