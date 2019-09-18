package sample.controller;

import animatefx.animation.LightSpeedIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.dao.SalesRecordsDao;
import sample.inteface.DispatcherController;
import sample.model.SalesRecords;
import sample.model.Salesman;
import sample.model.dto.ProductDto;
import sample.model.dto.SalesRecordsDto;
import sample.utility.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private FilterUtil filterUtil;
    private JFXTextField filterField;
    private ExcelUtil<SalesRecordsDto> excelUtil;
    private JFXButton toExcelBtn;
    private JFXButton deleteBtn;
    private Set<Integer> selectedRows;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colDetailId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDetailProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colDetailProductName.setCellFactory(TextFieldTableCell.forTableColumn());
        colDetailInputPrice.setCellValueFactory(new PropertyValueFactory<>("inputPrice"));
        colDetailOutPrice.setCellValueFactory(new PropertyValueFactory<>("outputPrice"));
        colDetailProductType.setCellValueFactory(new PropertyValueFactory<>("productTypeName"));
        colDetailSalesman.setCellValueFactory(new PropertyValueFactory<>("salesmanName"));
        colDetailDate.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        selectedRows = new HashSet<>();
        colDetailDate.setCellFactory(col -> new TableCell<SalesRecordsDto, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
//                        setText(formatter.format(item));
                    JFXDatePicker datePicker = new JFXDatePicker(LocalDate.now());
                    datePicker.setStyle("-fx-text-fill: black");
                    datePicker.setPromptText("Санани киритинг");
                    setGraphic(datePicker);
                }
            }
        });

        sales_records_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colDetailId.setMaxWidth(1f * Integer.MAX_VALUE * 5);
        colDetailProductName.setMaxWidth(1f * Integer.MAX_VALUE * 19);
        colDetailProductType.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        colDetailInputPrice.setMaxWidth(1f * Integer.MAX_VALUE * 8);
        colDetailOutPrice.setMaxWidth(1f * Integer.MAX_VALUE * 8);
        colDetailDate.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        colDetailSalesman.setMaxWidth(1f * Integer.MAX_VALUE * 25);
        colDetailAction.setMaxWidth(1f * Integer.MAX_VALUE * 5);

        Callback<TableColumn<SalesRecordsDto, String>, TableCell<SalesRecordsDto, String>> cellFactory = (param) -> {
            final TableCell<SalesRecordsDto, String> cell = new TableCell<SalesRecordsDto, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        final JFXCheckBox deleteBox = new JFXCheckBox();

                        deleteBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                            if (deleteBox.isSelected()){
                                System.out.println("row index: " +
                                        ((SalesRecordsDto)getTableRow().getItem()).getId());

                                selectedRows.add(((SalesRecordsDto)getTableRow().getItem()).getId());
                            }
                        });

                        setGraphic(deleteBox);

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

           if (records != null) {
               records.stream().forEach(salesRecords -> {
                   SalesRecordsDto dto = new SalesRecordsDto();
                   dto.setId(salesRecords.getId());
                   if (salesRecords.getSalesman()!=null){
                       dto.setSalesmanName(salesRecords.getSalesman().getFullName());
                   }
                   if (salesRecords.getProduct()!=null){
                       if (salesRecords.getProduct().getProductType()!=null){
                           dto.setProductTypeName(salesRecords.getProduct().getProductType().getProductType());
                       }
                       dto.setProductName(salesRecords.getProduct().getProductName());
                   }
                   dto.setInputPrice(salesRecords.getInputPrice());
                   dto.setOutputPrice(salesRecords.getOutputPrice());
                   dto.setDate(DateTimeUtil.convertToLocalDateTime(salesRecords.getDate()));

                   list.add(dto);
               });
           }
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtil.showAlert(Alert.AlertType.ERROR,
                    "Хатолик",
                    "Хатолик юз берди! ",
                    "Дастур билан боғлиқ хатолик юз берди!  \n" +
                            "Илтимос дастур администратори билан боғланинг! ");
        }

        sales_records_table.setEditable(true);

        sales_records_table.addEventFilter(KeyEvent.KEY_PRESSED, event -> {

            if (event.getCode() == KeyCode.ENTER){
                return;
            }

            if (sales_records_table.getEditingCell()==null){
                if (event.getCode().isLetterKey() || event.getCode().isDigitKey()){
                    TablePosition focusedCellPosition = sales_records_table.getFocusModel().getFocusedCell();
                    sales_records_table.edit(focusedCellPosition.getRow(),focusedCellPosition.getTableColumn());
                }
            }

        });

        sales_records_table.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ENTER){
                TablePosition pos = sales_records_table.getFocusModel().getFocusedCell();
                if (pos.getRow() == -1) {

                    sales_records_table.getSelectionModel().select(0);

                } else if (pos.getRow() == sales_records_table.getItems().size() -1) {
                    addRow();
                }else if (pos.getRow() < sales_records_table.getItems().size() -1) {
                    sales_records_table.getSelectionModel().clearAndSelect( pos.getRow() + 1, pos.getTableColumn());
                }
            }
        });

        sales_records_table.getSelectionModel().setCellSelectionEnabled(true);

        sales_records_table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        sales_records_table.setItems(list);

        sales_records_table.getSelectionModel().selectFirst();
    }

    public void addRow() {

        // get current position
        TablePosition pos = sales_records_table.getFocusModel().getFocusedCell();

        // clear current selection
        sales_records_table.getSelectionModel().clearSelection();

        // create new record and add it to the model
        SalesRecordsDto data = new SalesRecordsDto();
        data.setDate(LocalDateTime.now());
        list.add( data);

        // get last row
        int row = sales_records_table.getItems().size() - 1;
        sales_records_table.getSelectionModel().select( row, pos.getTableColumn());

        // scroll to new row
        sales_records_table.scrollTo( data);

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

    @Override
    public void setFilterField(JFXTextField filterField) {
        this.filterField = filterField;
        refreshFilter();
    }

    @Override
    public void setMainBtns(JFXButton btn,JFXButton deleteBtn) {
        this.toExcelBtn = btn;
        this.deleteBtn = deleteBtn;

        this.deleteBtn.setOnAction(event -> deleteSalesRecords());

        this.toExcelBtn.setOnAction(event -> {
            excelUtil = new ExcelUtil<>(sales_records_table,"sales_records",stage);
            excelUtil.exportToExcel();
        });
    }

    private void deleteSalesRecords(){
        System.out.println("deletable rows: " + selectedRows);

       boolean confirm = AlertUtil.showConfirmBox(Alert.AlertType.CONFIRMATION,
                "Ўчириш","Маҳсулотни ўчириш",
                "Ушбу: "+selectedRows+" маҳсулотни ўчиришни ҳоҳлайсизми?");

        if (confirm){
            Iterator iterator = selectedRows.iterator();
            while (iterator.hasNext()){
                try {
                    salesRecordsDao.deleteSalesRecord((Integer) iterator.next());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            populateSalesRecordsTable();
            refreshFilter();
        }
    }

    public void refreshFilter(){
        filterUtil = new FilterUtil(this.filterField,sales_records_table,list);
        filterUtil.initFilter();
    }
}
