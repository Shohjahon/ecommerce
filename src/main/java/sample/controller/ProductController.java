package sample.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import sample.dao.ProductDao;
import sample.inteface.DispatcherController;
import sample.model.Product;
import sample.model.dto.ProductDto;
import sample.utility.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
    private JFXTextField filterField;
    private FilterUtil filterUtil;
    private JFXButton toExcelBtn;
    private ExcelUtil<ProductDto> excelUtil;
    private static ProductController INSTANCE;
    private Executor executor;
    private ProgressIndicator progressIndicator;

    public ProductController(){
        INSTANCE =this;
    }

    public static ProductController getInstance(){
        return INSTANCE;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        executor = Executors.newCachedThreadPool((runnable) ->{
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);

            return thread;
        });

        productDao = DatabaseUtil.getProductDao();
        progressIndicator = new ProgressIndicator();

        colProductId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colProductType.setCellValueFactory(new PropertyValueFactory<>("productType"));
        colProductDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        product_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colProductId.setMaxWidth(1f*Integer.MAX_VALUE * 10);
        colProductName.setMaxWidth(1f*Integer.MAX_VALUE * 30);
        colProductType.setMaxWidth(1f*Integer.MAX_VALUE * 20);
        colProductDescription.setMaxWidth(1f*Integer.MAX_VALUE * 20);
        colPoruductAction.setMaxWidth(1f*Integer.MAX_VALUE * 30);

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
        product_table.setItems(list);

        populateProductTable();
    }

    public void populateProductTable(){
        list = FXCollections.observableArrayList();

        Task<List<Product>> productTask = new Task<List<Product>>() {
            @Override
            protected List<Product> call() throws Exception {
                return productDao.findAllProducts();
            }
        };

        productTask.setOnFailed(e -> {
            productTask.getException().printStackTrace();
            AlertUtil.showAlert(Alert.AlertType.ERROR,
                    "Хатолик",
                    "Хатолик юз берди! ",
                    "Дастур билан боғлиқ хатолик юз берди!  \n" +
                            "Илтимос дастур администратори билан боғланинг! ");
        });

        productTask.setOnSucceeded(e -> {
            List<Product> products = productTask.getValue();

            products.forEach(product -> {
                ProductDto productDto = new ProductDto();
                productDto.setId(product.getId());
                productDto.setProductName(product.getProductName());
                if (product.getProductType()!=null){
                    productDto.setProductType(product.getProductType().getProductType());
                }
                productDto.setDescription(product.getDescription());
                list.add(productDto);
            });

        });

        product_table.setPlaceholder(progressIndicator);

        executor.execute(productTask);
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
        this.filterField = filterField;
        filterUtil = new FilterUtil(this.filterField,product_table,list);
        filterUtil.initFilter();
    }

    @Override
    public void setMainBtns(JFXButton btn, JFXButton deleteBtn,JFXButton brief_btn) {
        this.toExcelBtn = btn;

        this.toExcelBtn.setOnAction(event -> {
            excelUtil = new ExcelUtil<>(product_table,"product",stage);
            excelUtil.exportToExcel();
        });
    }

    public void refreshFilter(){
        filterUtil = new FilterUtil(this.filterField,product_table,list);
        filterUtil.initFilter();
    }


}
