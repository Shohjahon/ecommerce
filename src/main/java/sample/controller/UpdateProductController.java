package sample.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
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
    private List<ProductType> productTypes;
    private ObservableList<String> list;
    private ObservableList<ProductDto> productDtos;
    private int index;
    private ProductDto product;
    @FXML
    private JFXTextField productNameField;
    @FXML
    private JFXComboBox productTypesBox;
    @FXML
    private JFXTextArea productDescriptionField;


    public void setData(ObservableList<ProductDto> productDtos,ProductDto product, int index){
        this.productDtos = productDtos;
        this.product = product;
        this.index = index;
        productTypesBox.getSelectionModel().select(product.getProductType());
        productNameField.setText(product.getProductName());
        productDescriptionField.setText(product.getDescription());
    }

    @Override
    public void setFilterField(JFXTextField filterField) {

    }

    @FXML
    public void handleUpdateProduct(){
        String pName = productNameField.getText();
        String des = productDescriptionField.getText();
        String pType = (String) productTypesBox.getSelectionModel().getSelectedItem();
        System.out.println("pType ----------------" + pType);
        if (!(pName.isEmpty() || pType.isEmpty())){
            product.setProductName(pName);
            product.setDescription(des);
            product.setProductType(pType);
            try {
                Product p = getProduct(product);
                System.out.println("------> " + p);
                productDao.updateProduct(p);
                productDtos.set(index,product);
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

    private Product getProduct(ProductDto productDto) throws Exception {
        ProductType productType = productTypeDao.findProductTypeByType(productDto.getProductType());
        Product product = new Product();
        product.setProductType(productType);
        product.setProductName(productDto.getProductName());
        product.setDescription(productDto.getDescription());
        product.setId(productDto.getId());
        return product;
    }

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
