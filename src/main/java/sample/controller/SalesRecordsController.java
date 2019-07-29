package sample.controller;

import animatefx.animation.LightSpeedIn;
import com.jfoenix.controls.JFXButton;
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
import sample.dao.SalesRecordsDao;
import sample.inteface.DispatcherController;
import sample.model.SalesRecords;
import sample.model.dto.SalesRecordsDto;
import sample.utility.AlertUtil;
import sample.utility.ControllerUtil;
import sample.utility.DatabaseUtil;
import sample.utility.DateTimeUtil;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Shoh Jahon tomonidan 7/26/2019 da qo'shilgan.
 */
public class SalesRecordsController implements Initializable,DispatcherController<SalesRecordsDto> {
    private Stage stage;
    @FXML
    private TableView<SalesRecordsDto> sales_records_table;
    @FXML
    private TableColumn<SalesRecordsDto,Integer> colDetailId;
    @FXML
    private TableColumn<SalesRecordsDto,String> colDetailProductName;
    @FXML
    private TableColumn<SalesRecordsDto,String> colDetailProductType;
    @FXML
    private TableColumn<SalesRecordsDto,Double> colDetailInputPrice;
    @FXML
    private TableColumn<SalesRecordsDto,Double> colDetailOutPrice;
    @FXML
    private TableColumn<SalesRecordsDto,String> colDetailSalesman;
    @FXML
    private TableColumn<SalesRecordsDto,LocalDateTime> colDetailDate;
    @FXML
    private TableColumn colDetailAction;
    private SalesRecordsDao salesRecordsDao;
    private ObservableList<SalesRecordsDto> list;
    private static SalesRecordsController INSTANCE;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colDetailId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDetailProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colDetailInputPrice.setCellValueFactory(new PropertyValueFactory<>("inputPrice"));
        colDetailOutPrice.setCellValueFactory(new PropertyValueFactory<>("outputPrice"));
        colDetailProductType.setCellValueFactory(new PropertyValueFactory<>("productTypeName"));
        colDetailSalesman.setCellValueFactory(new PropertyValueFactory<>("salesmanName"));
        colDetailDate.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        colDetailDate.setCellFactory(col -> new TableCell<SalesRecordsDto, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        sales_records_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colDetailId.setMaxWidth(1f * Integer.MAX_VALUE * 5);
        colDetailProductName.setMaxWidth(1f * Integer.MAX_VALUE * 15);
        colDetailProductType.setMaxWidth(1f * Integer.MAX_VALUE * 8);
        colDetailInputPrice.setMaxWidth(1f * Integer.MAX_VALUE * 8);
        colDetailOutPrice.setMaxWidth(1f * Integer.MAX_VALUE * 8);
        colDetailDate.setMaxWidth(1f * Integer.MAX_VALUE * 14);
        colDetailAction.setMaxWidth(1f * Integer.MAX_VALUE * 22);
        colDetailSalesman.setMaxWidth(1f * Integer.MAX_VALUE * 20);

        Callback<TableColumn<SalesRecordsDto, String>, TableCell<SalesRecordsDto, String>> cellFactory = (param) -> {
            final TableCell<SalesRecordsDto, String> cell = new TableCell<SalesRecordsDto, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        final JFXButton edit = new JFXButton("Таҳрирлаш");
                        edit.setStyle("-fx-background-color: #86C775;" +
                                "-fx-text-fill: white;" +
                                "-fx-min-width: 70px;");
                        final JFXButton delete = new JFXButton("Ўчириш");
                        delete.setStyle("-fx-background-color: #ff3333;" +
                                "-fx-text-fill: white;" +
                                "-fx-min-width: 70px");

                        edit.setOnAction(event -> {
                            SalesRecordsDto salesRecordsDto = sales_records_table.getItems().get(getIndex());
                            try {
                                ControllerUtil controllerUtil = new ControllerUtil(
                                        "/fxml/update/update_sales_record.fxml",
                                        600,700, list, salesRecordsDto,getIndex());

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
                            SalesRecordsDto dto = sales_records_table.getItems().get(getIndex());

                            try {
                                boolean confirm = AlertUtil.showConfirmBox(Alert.AlertType.CONFIRMATION,
                                        "Ўчириш",
                                        "Ҳисоботни ўчириш",
                                        "Ушбу ҳисоботни ўчиришни ҳоҳлайсизми?");

                                if (confirm){
                                    salesRecordsDao.deleteSalesRecord(dto.getId());
                                    list.remove(dto);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

                        HBox hBox = new HBox(10);
                        hBox.getChildren().addAll(edit, delete);
                        hBox.setAlignment(Pos.CENTER);
                        setGraphic(hBox);
                        setText(null);
                    }
                }
            };
            return cell;
        };
        colDetailAction.setCellFactory(cellFactory);
        salesRecordsDao = DatabaseUtil.getSalesRecordsDao();

        populateSalesRecordsTable();

    }

    public void populateSalesRecordsTable(){
        list = FXCollections.observableArrayList();

        try {
            List<SalesRecords> records = salesRecordsDao.findAllSalesRecords();

            System.out.println("sales records: ----> " + records);

            records.stream().forEach(salesRecords -> {
                SalesRecordsDto dto = new SalesRecordsDto();
                dto.setId(salesRecords.getId());
                dto.setSalesmanName(salesRecords.getSalesman().getFullName());
                dto.setProductTypeName(salesRecords.getProduct().getProductType().getProductType());
                dto.setProductName(salesRecords.getProduct().getProductName());
                dto.setInputPrice(salesRecords.getInputPrice());
                dto.setOutputPrice(salesRecords.getOutputPrice());
                dto.setDate(DateTimeUtil.convertToLocalDateTime(salesRecords.getDate()));

                list.add(dto);
            });
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showAlert(Alert.AlertType.ERROR,
                    "Хатолик",
                    "Хатолик юз берди! ",
                    "Дастур билан боғлиқ хатолик юз берди!  \n" +
                            "Илтимос дастур администратори билан боғланинг! ");
        }

        sales_records_table.setItems(list);
    }

    public SalesRecordsController(){
        INSTANCE = this;
    }

    public static SalesRecordsController getInstace(){
        return INSTANCE;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage= stage;
    }

    @Override
    public void setData(ObservableList<SalesRecordsDto> list, SalesRecordsDto dto, int index) {

    }
}
