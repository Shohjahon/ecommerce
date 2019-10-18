package sample.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.dao.SalesmanDao;
import sample.inteface.DispatcherController;
import sample.model.Salesman;
import sample.model.dto.SalesmanDto;
import sample.utility.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Shoh Jahon tomonidan 7/26/2019 da qo'shilgan.
 */
public class SalesmanController implements Initializable,DispatcherController<SalesmanDto>{
    private Stage stage;
    @FXML
    private TableView<SalesmanDto> salesman_table;
    @FXML
    private TableColumn<SalesmanDto,Integer> colSalesmanId;
    @FXML
    private TableColumn<SalesmanDto,String> colSalesmanName;
    @FXML
    private TableColumn<SalesmanDto,String> colSalesmanPhone;
    @FXML
    private TableColumn<SalesmanDto,String> colSalesmanAddress;
    @FXML
    private TableColumn colSalesmanAction;
    private SalesmanDao salesmanDao;
    private ObservableList<SalesmanDto> list;
    private static SalesmanController INSTANCE;
    private JFXTextField filterField;
    private FilterUtil filterUtil;
    private JFXButton toExcelBtn;
    private ExcelUtil<SalesmanDto> excelUtil;
    private Executor executor;
    private ProgressIndicator progressIndicator;

    public SalesmanController(){
        INSTANCE = this;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        executor = Executors.newCachedThreadPool((runnable) ->{
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });

        colSalesmanId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSalesmanName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colSalesmanPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colSalesmanAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        salesman_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        colSalesmanId.setMaxWidth(1f*Integer.MAX_VALUE * 10);
        colSalesmanName.setMaxWidth(1f*Integer.MAX_VALUE * 30);
        colSalesmanPhone.setMaxWidth(1f*Integer.MAX_VALUE * 20);
        colSalesmanAddress.setMaxWidth(1f*Integer.MAX_VALUE * 20);
        colSalesmanAction.setMaxWidth(1f*Integer.MAX_VALUE * 20);

        Callback<TableColumn<SalesmanDto,String>, TableCell<SalesmanDto,String>> cellFactory = (param)->{
            final TableCell<SalesmanDto,String> cell = new TableCell<SalesmanDto,String>(){
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

                        edit.setOnAction(event -> {
                            SalesmanDto salesmanDto = salesman_table.getItems().get(getIndex());
                            try {
                                ControllerUtil controllerUtil =
                                        new ControllerUtil("/fxml/update/update_salesman.fxml",
                                                600,500,
                                                list,salesmanDto,getIndex());
                                controllerUtil.showWindow();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                        delete.setOnAction(event -> {
                            SalesmanDto dto = salesman_table.getItems().get(getIndex());

                            try {
                                boolean confirm = AlertUtil.showConfirmBox(Alert.AlertType.CONFIRMATION,
                                        "Ўчириш",
                                        "Сотувчини ўчириш",
                                        "Ушбу сотувчини ўчиришни ҳоҳлайсизми?");
                                if (confirm){
                                    salesmanDao.deleteSalesman(dto.getId());
                                    list.remove(dto);
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

        colSalesmanAction.setCellFactory(cellFactory);
        salesmanDao = DatabaseUtil.getSalesmanDao();

        salesman_table.setItems(list);

        populateSalesmanTable();

    }


    public void populateSalesmanTable(){
        list = FXCollections.observableArrayList();

        Task<List<Salesman>> salesmanTask = new Task<List<Salesman>>() {
            @Override
            protected List<Salesman> call() throws Exception {
                return salesmanDao.findAllSalesmans();
            }
        };

        salesmanTask.setOnFailed(e -> AlertUtil.showAlert(Alert.AlertType.ERROR,
                "Хатолик",
                "Хатолик юз берди! ",
                "Дастур билан боғлиқ хатолик юз берди!  \n" +
                        "Илтимос дастур администратори билан боғланинг! "));

        salesmanTask.setOnSucceeded(e -> {
                    List<Salesman> salesmen = salesmanTask.getValue();

                    salesmen.stream().forEach(salesman -> {
                        SalesmanDto dto = new SalesmanDto();
                        dto.setId(salesman.getId());
                        dto.setFullName(salesman.getFullName());
                        dto.setPhoneNumber(salesman.getPhoneNumber());
                        dto.setAddress(salesman.getAddress());

                        list.add(dto);
                    });
                });

        executor.execute(salesmanTask);

        salesman_table.setPlaceholder(progressIndicator);

    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setData(ObservableList<SalesmanDto> list, SalesmanDto dto, int index) {

    }

    @Override
    public void setFilterField(JFXTextField filterField) {
        this.filterField = filterField;
        filterUtil = new FilterUtil(this.filterField,salesman_table,list);
        filterUtil.initFilter();
    }

    @Override
    public void setMainBtns(JFXButton btn,JFXButton deleteBtn,JFXButton brief_btn) {
        this.toExcelBtn = btn;

        this.toExcelBtn.setOnAction(event -> {
            excelUtil = new ExcelUtil<>(salesman_table,"salesman",stage);
            excelUtil.exportToExcel();
        });
    }

    public void refreshFilter(){
        if (filterUtil == null){
            filterUtil = new FilterUtil(this.filterField,salesman_table,list);
        }
        filterUtil.initFilter();
    }

    public static SalesmanController getInstance(){
        return INSTANCE;
    }
}
