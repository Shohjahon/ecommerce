package sample.controller;

import animatefx.animation.*;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import sample.dao.ProductDao;
import sample.inteface.DispatcherController;
import sample.model.Product;
import sample.model.dto.ProductDto;
import sample.utility.AlertUtil;
import sample.utility.ControllerUtil;
import sample.utility.DatabaseUtil;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Shoh Jahon tomonidan 7/22/2019 da qo'shilgan.
 */
public class ProductController implements Initializable,DispatcherController<ProductDto> {
    private Stage stage;
    @FXML
    private TableView<ProductDto> product_table;
    @FXML
    private TableColumn<ProductDto,Integer> colProductId;
    @FXML
    private TableColumn<ProductDto,String> colProductName;
    @FXML
    private TableColumn<ProductDto,String> colProductDescription;
    @FXML
    private TableColumn<ProductDto,String> colProductType;
    @FXML
    private TableColumn colPoruductAction;

    private ProductDao productDao;
    private ObservableList<ProductDto> list;
    private static ProductController INSTANCE;
    private UpdateProductController updateProductController;

    public ProductController(){
        INSTANCE =this;
    }

    public static ProductController getInstance(){
        return INSTANCE;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colProductId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colProductType.setCellValueFactory(new PropertyValueFactory<>("productType"));
        colProductDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        product_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colProductId.setMaxWidth(1f*Integer.MAX_VALUE * 10);
        colProductName.setMaxWidth(1f*Integer.MAX_VALUE * 20);
        colProductType.setMaxWidth(1f*Integer.MAX_VALUE * 20);
        colProductDescription.setMaxWidth(1f*Integer.MAX_VALUE * 30);
        colPoruductAction.setMaxWidth(1f*Integer.MAX_VALUE * 20);

        Callback<TableColumn<ProductDto,String>,TableCell<ProductDto,String>> cellFactory = (param)->{
          final TableCell<ProductDto,String> cell = new TableCell<ProductDto,String>(){
              @Override
              protected void updateItem(String item, boolean empty) {
                  super.updateItem(item, empty);
                  if (empty){
                      setGraphic(null);
                      setText(null);
                  }else {
                      final JFXButton edit = new JFXButton("Таҳрирлаш");
                      edit.setStyle("-fx-background-color: #86C775;" +
                              "-fx-text-fill: white;" +
                              "-fx-min-width: 70px;");
                      final JFXButton delete = new JFXButton("Ўчириш");
                      delete.setStyle("-fx-background-color: #ff3333;" +
                              "-fx-text-fill: white;" +
                              "-fx-min-width: 70px");

                      edit.setOnAction(event->{
                          ProductDto product = product_table.getItems().get(getIndex());
                          try {
                              ControllerUtil controllerUtil = new ControllerUtil(
                                      "/fxml/update/update_product.fxml",
                                      600,500,list,product,getIndex());
                              controllerUtil.showWindow();
                          } catch (IOException e) {
                              e.printStackTrace();
                              AlertUtil.showAlert(Alert.AlertType.ERROR,
                                      "Хатолик",
                                      "Хатолик юз берди! ",
                                      "Дастур   боғлиқ хатолик юз берди!  \n" +
                                              "Илтимос дастур администратори билан боғланинг! ");
                          }
                      });
                      delete.setOnAction(event -> {
                          ProductDto productDto = product_table.getItems().get(getIndex());
                          try {
                              boolean confirm = AlertUtil.showConfirmBox(Alert.AlertType.CONFIRMATION,
                                      "Ўчириш",
                                      "Маҳсулотни ўчириш",
                                      "Ушбу маҳсулотни ўчиришни ҳоҳлайсизми?");
                              if (confirm){
                                  productDao.deleteProduct(productDto.getId());
                                  list.remove(productDto);
                              }
                          } catch (Exception e) {
                              e.printStackTrace();
                          }
                      });
                      HBox hBox = new HBox(10);
                      hBox.getChildren().addAll(edit,delete);
                      hBox.setAlignment(Pos.CENTER);
                      setGraphic(hBox);
                      setText(null);
                  }
              }
          };
          return cell;
        };
        colPoruductAction.setCellFactory(cellFactory);
        populateProductTable();
    }

    public void populateProductTable(){
        list = FXCollections.observableArrayList();
        productDao = DatabaseUtil.getProductDao();

        try {
            System.out.println("products: " + productDao.findAllProducts());
            productDao.findAllProducts().stream().forEach(product -> {
                ProductDto productDto = new ProductDto();
                productDto.setId(product.getId());
                productDto.setProductName(product.getProductName());
                productDto.setProductType(product.getProductType().getProductType());
                productDto.setDescription(product.getDescription());
                list.add(productDto);
            });
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showAlert(Alert.AlertType.ERROR,
                    "Хатолик",
                    "Хатолик юз берди! ",
                    "Дастур билан боғлиқ хатолик юз берди!  \n" +
                            "Илтимос дастур администратори билан боғланинг! ");
        }
        product_table.setItems(list);
    }
    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setData(ObservableList<ProductDto> list, ProductDto dto, int index) {

    }
}
