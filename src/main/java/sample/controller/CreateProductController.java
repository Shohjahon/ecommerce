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
import sample.dao.ProductDao;
import sample.dao.ProductTypeDao;
import sample.inteface.DispatcherController;
import sample.model.Product;
import sample.model.ProductType;
import sample.model.dto.ProductDto;
import sample.utility.AlertUtil;
import sample.utility.DatabaseUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Shoh Jahon tomonidan 7/22/2019 da qo'shilgan.
 */
public class CreateProductController implements Initializable,DispatcherController<ProductDto>{
    private Stage stage;
    @FXML
    private JFXButton cancelBtn;
    @FXML
    private JFXButton addBtn;
    private double dragAnchorY;
    private double dragAnchorX;
    private ProductDao productDao;
    private ProductTypeDao productTypeDao;
    private List<ProductType> productTypes;
    private ObservableList<String> list;
    @FXML
    private JFXTextField productNameField;
    @FXML
    private JFXComboBox productTypesBox;
    @FXML
    private JFXTextArea productDescriptionField;
    private ProductController productController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        list = FXCollections.observableArrayList();
        productDao = DatabaseUtil.getProductDao();
        productTypeDao = DatabaseUtil.getProductTypeDao();
        try {
            productTypes = productTypeDao.findAllProductTypes();
            productTypes.stream().forEach(productType -> list.add(productType.getProductType()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        productTypesBox.setItems(list);

//        productQuantityField.textProperty().addListener(new ChangeListener<String>() {
//            @Override
//            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//                if (!newValue.matches("\\d*")){
//                    productQuantityField.setText(newValue.replaceAll("[^\\d]", ""));
//                }
//            }
//        });
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setData(ObservableList<ProductDto> list, ProductDto dto, int index) {

    }

    @Override
    public void setFilterField(JFXTextField filterField) {

    }

    @Override
    public void setMainBtns(JFXButton export, JFXButton deleteBtn,JFXButton brief_btn) {

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


    @FXML
    public void createProduct(){
        Product product = new Product();
        String selectedItem = (String) productTypesBox.getSelectionModel().getSelectedItem();
        final ProductType[] productType = new ProductType[1];
        productTypes.stream().forEach(productType1 -> {if (productType1.getProductType().equals(selectedItem)) {
            productType[0] =productType1;
        }});
        String productName = productNameField.getText();
        String productDescription = productDescriptionField.getText();

        if (productType.length > 0 && !(productName.isEmpty() && productDescription.isEmpty())) {
            product.setProductName(productName);
            product.setDescription(productDescription);
            product.setProductType(productType[0]);
            try {
                productDao.insertProduct(product);
                productController = ProductController.getInstance();
                productController.populateProductTable();
                handleCancel();
                productController.refreshFilter();
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
}
