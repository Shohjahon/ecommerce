package sample.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sample.dao.ProductDao;
import sample.dao.SalesRecordsDao;
import sample.dao.SalesmanDao;
import sample.inteface.DispatcherController;
import sample.listener.ComboBoxAutoComplete;
import sample.model.Product;
import sample.model.SalesRecords;
import sample.model.Salesman;
import sample.model.dto.SalesRecordsDto;
import sample.utility.AlertUtil;
import sample.utility.DatabaseUtil;
import sample.utility.DateTimeUtil;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Shoh Jahon tomonidan 7/29/2019 da qo'shilgan.
 */
public class UpdateSalesRecordController implements Initializable,DispatcherController<SalesRecordsDto> {
    private Stage stage;
    private double dragAnchorY;
    private double dragAnchorX;
    @FXML
    private JFXButton cancelBtn;
    @FXML
    private JFXButton addBtn;
    @FXML
    private JFXComboBox productBox;
    @FXML
    private JFXComboBox salesmanBox;
    @FXML
    private JFXDatePicker dateDetailPicker;
    @FXML
    private JFXTextField inDetailField;
    @FXML
    private JFXTextField outDetailField;
    private ProductDao productDao;
    private SalesmanDao salesmanDao;
    private SalesRecordsDao salesRecordsDao;
    private ObservableList<Product> productList;
    private ObservableList<Salesman> salesmanList;
    private SalesRecordsController salesRecordsController;
    private ObservableList<SalesRecordsDto> salesRecordsDtos;
    private SalesRecordsDto salesRecordsDto;
    private int index;

    @FXML
    public void handleUpdateSalesRecord(){
        Date date = DateTimeUtil.convertToDate(dateDetailPicker.getValue());
        String inCost = inDetailField.getText();
        String outCost = outDetailField.getText();
        Product product = (Product) productBox.getValue();
        Salesman salesman = (Salesman) salesmanBox.getValue();

        boolean isValid = !((inCost.isEmpty() || outCost.isEmpty()) &&
                (product ==null || salesman ==null));

        if (isValid){
            SalesRecords salesRecord = new SalesRecords();
            salesRecord.setId(salesRecordsDto.getId());
            salesRecord.setProduct(product);
            salesRecord.setSalesman(salesman);
            salesRecord.setInputPrice(Double.valueOf(inCost));
            salesRecord.setOutputPrice(Double.valueOf(outCost));
            salesRecord.setDate(date);

            try {
                salesRecordsDao.updateSalesRecord(salesRecord);
                salesRecordsDtos.set(index,SalesRecordsDto.mapToSalesRecordsDto(salesRecord));
                handleCancel();
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productList = FXCollections.observableArrayList();
        salesmanList = FXCollections.observableArrayList();

        productDao = DatabaseUtil.getProductDao();
        salesmanDao = DatabaseUtil.getSalesmanDao();
        salesRecordsDao = DatabaseUtil.getSalesRecordsDao();

        try {
            productList.addAll(productDao.findAllProducts());
            salesmanList.addAll(salesmanDao.findAllSalesmans());
        } catch (Exception e) {
            e.printStackTrace();
        }

        productBox.setItems(productList);
        salesmanBox.setItems(salesmanList);

        new ComboBoxAutoComplete<String>(productBox);
        new ComboBoxAutoComplete<String>(salesmanBox);

        dateDetailPicker.setConverter(new StringConverter<LocalDate>() {
            String pattern = "dd.MM.yyyy";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setData(ObservableList<SalesRecordsDto> list, SalesRecordsDto dto, int index) {
        this.salesRecordsDtos = list;
        this.salesRecordsDto = dto;
        this.index = index;

        try {
            SalesRecords salesRecords = salesRecordsDao.findSalesRecorById(dto.getId());
            productBox.getSelectionModel().select(salesRecords.getProduct());
            salesmanBox.getSelectionModel().select(salesRecords.getSalesman());
            inDetailField.setText(String.valueOf(dto.getInputPrice()));
            outDetailField.setText(String.valueOf(dto.getInputPrice()));
            dateDetailPicker.setValue(LocalDate.from(dto.getDate()));
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
