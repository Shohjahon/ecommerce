package sample.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.log4j.Logger;
import sample.dao.ProductTypeDao;
import sample.inteface.DispatcherController;
import sample.model.ProductType;
import sample.utility.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Shoh Jahon tomonidan 6/27/2019 da qo'shilgan.
 */
public class ProductTypeController implements Initializable , DispatcherController<ProductType>{
    @FXML
    private TableView<ProductType> product_type_table;
    @FXML
    private TableColumn<ProductType , Integer> colProductTypeId;
    @FXML
    private TableColumn<ProductType , String>  colProductTypeName;
    @FXML
    private TableColumn colAction;
    private ProductTypeDao productTypeDao;
    private ObservableList<ProductType> list;
    private Logger logger = Logger.getLogger(getClass());
    private Stage stage;
    private static ProductTypeController INSTANCE;
    private FilterUtil filterUtil;
    private JFXTextField filterField;
    private JFXButton toExcelBtn;
    private ExcelUtil<ProductType> excelUtil;


    public ProductTypeController(){
        INSTANCE = this;
    }

    public static ProductTypeController getInstance(){
        return INSTANCE;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setData(ObservableList<ProductType> list, ProductType dto, int index) {

    }

    @Override
    public void setFilterField(JFXTextField filterField) {
        this.filterField = filterField;
        filterUtil = new FilterUtil(this.filterField,product_type_table,list);
        filterUtil.initFilter();
    }

    @Override
    public void setMainBtns(JFXButton btn,JFXButton deleteBtn,JFXButton brief_btn) {
        this.toExcelBtn = btn;

        this.toExcelBtn.setOnAction(event -> {
            excelUtil = new ExcelUtil<>(product_type_table,"product_type",stage);
            excelUtil.exportToExcel();
        });
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colProductTypeId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProductTypeName.setCellValueFactory(new PropertyValueFactory<>("productType"));

        product_type_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colProductTypeId.setMaxWidth(1f*Integer.MAX_VALUE * 20);
        colProductTypeName.setMaxWidth(1f*Integer.MAX_VALUE * 50);
        colAction.setMaxWidth(1f*Integer.MAX_VALUE * 30);

        Callback<TableColumn<ProductType,String>,TableCell<ProductType,String>> cellFactory = (param) -> {
            final TableCell<ProductType,String> cell = new TableCell<ProductType,String>(){
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
                            ProductType productType = product_type_table.getItems().get(getIndex());
                            try {
                                ControllerUtil controllerUtil = new ControllerUtil(
                                        "/fxml/update/update_product_type.fxml",
                                        600,500,list,productType,getIndex());
                                controllerUtil.showWindow();
                            } catch (IOException e) {
                                e.printStackTrace();
                                AlertUtil.showAlert(Alert.AlertType.ERROR,
                                        "Хатолик",
                                        "Хатолик юз берди! ",
                                        "Дастур билан боғлиқ хатолик юз берди!  \n" +
                                                "Илтимос дастур администратори билан боғланинг! ");
                            }

                        });

                        delete.setOnAction(event->{
                            ProductType productType = product_type_table.getItems().get(getIndex());
                            try {
                                boolean confirm = AlertUtil.showConfirmBox(Alert.AlertType.CONFIRMATION,
                                        "Ўчириш",
                                        "Маҳсулот турини ўчириш",
                                        "Ушбу маҳсулот турини ўчиришни ҳоҳлайсизми?");
                                if (confirm){
                                    productTypeDao.deleteProductTypeById(productType.getId());
                                    list.remove(productType);
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

        colAction.setCellFactory(cellFactory);

        populateTableView();
    }

    public void populateTableView(){
        list = FXCollections.observableArrayList();
        productTypeDao = DatabaseUtil.getProductTypeDao();
        try {
            productTypeDao.findAllProductTypes().stream().forEach(productType -> list.add(productType));
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showAlert(Alert.AlertType.ERROR,
                    "Хатолик",
                    "Хатолик юз берди! ",
                    "Дастур билан боғлиқ хатолик юз берди!  \n" +
                            "Илтимос дастур администратори билан боғланинг! ");
        }
        product_type_table.setItems(list);

    }

    public void refreshFilter(){
        filterUtil = new FilterUtil(this.filterField,product_type_table,list);
        filterUtil.initFilter();
    }
}
