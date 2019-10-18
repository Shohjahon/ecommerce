package sample.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import org.apache.log4j.Logger;
import sample.dao.SalesRecordsDao;
import sample.inteface.DispatcherController;
import sample.model.Product;
import sample.model.ProductType;
import sample.model.SalesRecords;
import sample.model.Salesman;
import sample.model.dto.SalesRecordsDto;
import sample.utility.*;
import tornadofx.control.DatePickerTableCell;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Shoh Jahon tomonidan 7/26/2019 da qo'shilgan.
 */
public class SalesRecordsController implements Initializable,DispatcherController<SalesRecordsDto>{
    private Stage stage;
    @FXML
    private TableView<SalesRecordsDto> sales_records_table;
    @FXML
    private TableColumn<SalesRecordsDto,Integer> colDetailId;
    @FXML
    private TableColumn<SalesRecordsDto,Product> colDetailProductName;
    @FXML
    private TableColumn<SalesRecordsDto,ProductType> colDetailProductType;
    @FXML
    private TableColumn<SalesRecordsDto,Double> colDetailInputPrice;
    @FXML
    private TableColumn<SalesRecordsDto,String> colDetailOutPrice;
    @FXML
    private TableColumn<SalesRecordsDto,Salesman> colDetailSalesman;
    @FXML
    private TableColumn<SalesRecordsDto,LocalDate> colDetailDate;
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
    private JFXButton brief_btn;
    private Set<Integer> selectedRows;
    private ObservableList<Product> products = FXCollections.observableArrayList();
    private ObservableList<Salesman> salesmen = FXCollections.observableArrayList();

    private Logger logger = Logger.getLogger(getClass());
    private Executor executor;
    private ProgressIndicator progressIndicator;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initComboList();

        progressIndicator = new ProgressIndicator();

        executor = Executors.newCachedThreadPool((runnable) ->{
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });

        logger.info("inside initialize method");

        colDetailId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDetailProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colDetailInputPrice.setCellValueFactory(new PropertyValueFactory<>("inputPrice"));
        colDetailOutPrice.setCellValueFactory(cellData-> Bindings.format("%.2f",cellData.getValue().getOutputPrice()));
        colDetailProductType.setCellValueFactory(new PropertyValueFactory<>("productTypeName"));
        colDetailSalesman.setCellValueFactory(new PropertyValueFactory<>("salesmanName"));
        colDetailDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        colDetailInputPrice.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        colDetailDate.setCellFactory(DatePickerTableCell.forTableColumn(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate object) {
                if (object != null){
                    return object.format(formatter);
                }
                return null;
            }

            @Override
            public LocalDate fromString(String string) {
                return null;
            }
        }));

        colDetailProductName.setCellFactory(ComboBoxTableCell.forTableColumn(new StringConverter<Product>() {
            @Override
            public String toString(Product object) {
                if (object != null){
                    return object.getProductName();
                }
                return null;
            }

            @Override
            public Product fromString(String string) {
                return null;
            }
        },products));


        colDetailSalesman.setCellFactory(ComboBoxTableCell.forTableColumn(new StringConverter<Salesman>() {
            @Override
            public String toString(Salesman object) {
                if (object != null){
                    return object.getFullName();
                }
                return "";
            }

            @Override
            public Salesman fromString(String string) {
                return null;
            }
        },salesmen));

        colDetailInputPrice.setOnEditCommit(e ->{
            SalesRecordsDto record = e.getTableView().getItems().get(e.getTablePosition().getRow());
            SalesRecords salesRecords = null;
            try {
                salesRecords = DatabaseUtil.getSalesRecordsDao().findSalesRecorById(record.getId());
                salesRecords.setInputPrice(e.getNewValue());
            } catch (Exception e1) {
                logger.error("Error",e1);
            }

            ValidationUtil.insertOrUpdate(salesRecords,list);

            refreshFilter();
        });

        colDetailProductName.setOnEditCommit(e -> {
            SalesRecordsDto record = e.getTableView().getItems().get(e.getTablePosition().getRow());
            SalesRecords salesRecords = null;
            try {
                salesRecords = DatabaseUtil.getSalesRecordsDao().findSalesRecorById(record.getId());
                salesRecords.setProduct(e.getNewValue());
            } catch (Exception e1) {
                logger.error("Error",e1);
            }

            ValidationUtil.insertOrUpdate(salesRecords,list);

            refreshFilter();
        });

        colDetailDate.setOnEditCommit(e -> {
            SalesRecordsDto record = e.getTableView().getItems().get(e.getTablePosition().getRow());
            SalesRecords salesRecords = null;
            try {
                salesRecords = DatabaseUtil.getSalesRecordsDao().findSalesRecorById(record.getId());
                salesRecords.setDate(DateTimeUtil.convertToDate(e.getNewValue()));
            } catch (Exception e1) {
                logger.error("Error",e1);
            }

            ValidationUtil.insertOrUpdate(salesRecords,list);

            refreshFilter();
        });


        colDetailSalesman.setOnEditCommit(e -> {
            SalesRecordsDto record = e.getTableView().getItems().get(e.getTablePosition().getRow());
            SalesRecords salesRecords = null;
            try {
                salesRecords = DatabaseUtil.getSalesRecordsDao().findSalesRecorById(record.getId());
                salesRecords.setSalesman(e.getNewValue());
            } catch (Exception e1) {
                logger.error("Error",e1);
            }

            ValidationUtil.insertOrUpdate(salesRecords,list);

            refreshFilter();
        });

        selectedRows = new HashSet<>();

        sales_records_table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        colDetailId.setMaxWidth(1f * Integer.MAX_VALUE * 5);
        colDetailProductName.setMaxWidth(1f * Integer.MAX_VALUE * 19);
        colDetailProductType.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        colDetailInputPrice.setMaxWidth(1f * Integer.MAX_VALUE * 8);
        colDetailOutPrice.setMaxWidth(1f * Integer.MAX_VALUE * 8);
        colDetailDate.setMaxWidth(1f * Integer.MAX_VALUE * 10);
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
                                selectedRows.add(((SalesRecordsDto)getTableRow().getItem()).getId());
                            }else {
                                selectedRows.remove(((SalesRecordsDto)getTableRow().getItem()).getId());
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
                    try {
                        addRow();
                    } catch (Exception e) {
                        logger.error("Error",e);
                        AlertUtil.showAlert(Alert.AlertType.ERROR,"Ҳатолик","Янги қатор қўшишда хатолик пайдо бўлди!","Ушбу муаммони йечиш учун дастур яратувчиси билан боғланинг! ");
                    }
                }else if (pos.getRow() < sales_records_table.getItems().size() -1) {
                    sales_records_table.getSelectionModel().clearAndSelect( pos.getRow() + 1, pos.getTableColumn());
                }
            }
        });

        sales_records_table.getSelectionModel().setCellSelectionEnabled(true);

        sales_records_table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        sales_records_table.setItems(list);

        sales_records_table.getSelectionModel().selectFirst();

        logger.info("end of initialize method");

    }


    private void initComboList(){
        try {
            List<Product> p = DatabaseUtil.getProductDao().findAllProducts();
            List<Salesman> s = DatabaseUtil.getSalesmanDao().findAllSalesmans();

            products.addAll(p);
            salesmen.addAll(s);
        } catch (Exception e) {
            logger.error("Error",e);
        }
    }

    public void populateSalesRecordsTable(){
        list = FXCollections.observableArrayList();

        Task<List<SalesRecords>> salesTask = new Task<List<SalesRecords>>() {
            @Override
            protected List<SalesRecords> call() throws Exception {
                return salesRecordsDao.findAllSalesRecords();
            }
        };

        logger.info("inside populateSalesRecords() method");

        salesTask.setOnFailed(e -> {
            logger.error("Error",salesTask.getException());
            AlertUtil.showAlert(Alert.AlertType.ERROR,
                    "Хатолик",
                    "Хатолик юз берди! ",
                    "Дастур билан боғлиқ хатолик юз берди!  \n" +
                            "Илтимос дастур администратори билан боғланинг! ");
        });

        salesTask.setOnSucceeded(e -> {
            List<SalesRecords> salesRecords = salesTask.getValue();

            if (salesRecords != null ) {
                if (salesRecords.size() != 0){
                    fillList(salesRecords);
                }else {
                    try {
                        addInitialRow(salesRecords);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

            }else {
                // create new record and add it to the model
                try {
                    addInitialRow(salesRecords);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        executor.execute(salesTask);

        sales_records_table.setPlaceholder(progressIndicator);

        selectedRows.clear();

        logger.debug("end of populateSalesRecord() method");
    }

    private void addInitialRow(List<SalesRecords> records) throws Exception {
        // create new record and add it to the model
        SalesRecordsDto data = new SalesRecordsDto();

        data.setDate(LocalDate.now());

        SalesRecords salesRecord = SalesRecordsDto.mapToSalesRecords(data);
        salesRecord.setSellingCoefficient(30.0);

        salesRecordsDao.insertSalesRecord(salesRecord);

        records = salesRecordsDao.findAllSalesRecords();

        if (records!=null){
            fillList(records);
        }
    }

    private void fillList(List<SalesRecords> records){
        records.forEach(salesRecords -> {
            SalesRecordsDto dto = new SalesRecordsDto();
            dto.setId(salesRecords.getId());
            if (salesRecords.getSalesman()!=null){
                dto.setSalesmanName(salesRecords.getSalesman());
            }
            if (salesRecords.getProduct()!=null){
                if (salesRecords.getProduct().getProductType()!=null){
                    dto.setProductTypeName(salesRecords.getProduct().getProductType());
                }
                dto.setProductName(salesRecords.getProduct());
            }
            dto.setInputPrice(salesRecords.getInputPrice());
            dto.setOutputPrice(salesRecords.getInputPrice() * ((100 + salesRecords.getSellingCoefficient()) / 100));
            if (salesRecords.getDate() != null){
                dto.setDate(DateTimeUtil.convertToLocalDate(salesRecords.getDate()));
            }

            list.add(dto);
        });
    }

    public void addRow() throws Exception {

        // get current position
        TablePosition pos = sales_records_table.getFocusModel().getFocusedCell();

        // clear current selection
        sales_records_table.getSelectionModel().clearSelection();

        // create new record and add it to the model
        SalesRecordsDto data = new SalesRecordsDto();

        data.setDate(LocalDate.now());

        int id = salesRecordsDao.insertSalesRecord(SalesRecordsDto.mapToSalesRecords(data));

//        populateSalesRecordsTable();

        if (id != 0){
            SalesRecords s = salesRecordsDao.findSalesRecorById(id);
            data = SalesRecordsDto.mapToSalesRecordsDto(s);
            list.add(data);
        }

        refreshFilter();

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
    public void setMainBtns(JFXButton btn,JFXButton deleteBtn,JFXButton brief_btn) {
        this.toExcelBtn = btn;
        this.deleteBtn = deleteBtn;
        this.brief_btn = brief_btn;

        this.deleteBtn.setOnAction(event -> deleteSalesRecords());

        this.toExcelBtn.setOnAction(event -> {
            excelUtil = new ExcelUtil<>(sales_records_table,"sales_records",stage);
            excelUtil.exportToExcel();
        });

        this.brief_btn.setOnAction(event -> openBriefWindow());
    }

    private void openBriefWindow(){
        FXMLLoader loader = new FXMLLoader(SalesRecordsController.class.getResource("/fxml/product_brief.fxml"));
        try {
            Iterator iterator = selectedRows.iterator();

            if (selectedRows.size() == 1 && iterator.hasNext()){
                Parent parent = loader.load();
                Scene scene = new Scene(parent,800,550);
                final BriefController controller = loader.getController();
                StageStyle style = StageStyle.TRANSPARENT;
                Stage stage = new Stage(style);
                stage.setScene(scene);
                controller.setStage(stage,scene, (Integer) iterator.next(),sales_records_table);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.show();
            }else {
                AlertUtil.showAlert(Alert.AlertType.WARNING,"Диққат","Хатолик! ",
                        "Қуйидаги листдан ҳеч қандай элемент танланмаган,  Ҳисоботлардан бири ҳақида батафсил билиш учун,  қуйидагилардан фақат биттасини танланг! ");
            }
        } catch (IOException e) {
            logger.error("Error",e);
        }
    }

    private void deleteSalesRecords(){
        if (selectedRows.size() == 0){
            AlertUtil.showConfirmBox(Alert.AlertType.WARNING,
                    "Ўчириш","Маҳсулотни ўчириш",
                    "Маҳсулотни ўчириш учун камида битта маҳсулот танланган бўлиши керак!");

            return;
        }

        boolean confirm = AlertUtil.showConfirmBox(Alert.AlertType.CONFIRMATION,
                "Ўчириш","Маҳсулотни ўчириш",
                "Ушбу ID лари -> "+selectedRows+" бўлган маҳсулотни ўчиришни ҳоҳлайсизми?");

        if (confirm){
            Iterator iterator = selectedRows.iterator();
            while (iterator.hasNext()){
                try {
                    salesRecordsDao.deleteSalesRecord((Integer) iterator.next());
                } catch (Exception e) {
                    logger.error("Error",e);
                }
            }

            populateSalesRecordsTable();
            refreshFilter();
        }
    }

    public void refreshFilter(){
        filterUtil = new FilterUtil(this.filterField,sales_records_table,this.list);
        filterUtil.initFilter();
        filterUtil.setSelectedRowsAndClear(selectedRows);

        sales_records_table.scrollTo(sales_records_table.getItems().size()-1);
    }
}
