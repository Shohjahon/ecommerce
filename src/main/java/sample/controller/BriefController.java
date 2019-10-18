package sample.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import sample.model.Product;
import sample.model.SalesRecords;
import sample.model.Salesman;
import sample.model.Statistics;
import sample.model.dto.SalesRecordsDto;
import sample.utility.AlertUtil;
import sample.utility.DatabaseUtil;
import sample.utility.DateTimeUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Shoh Jahon tomonidan 10/9/2019 da qo'shilgan.
 */
public class BriefController implements Initializable{
    private Stage stage;
    private Scene scene;
    private double dragAnchorY;
    private double dragAnchorX;
    private boolean isFullScreen = false;
    private Integer recordId;
    @FXML
    private ImageView record_image;
    @FXML
    private JFXButton upload_img;
    @FXML
    private ComboBox<Product> product_box;
    @FXML
    private ComboBox<Salesman> salesman_box;
    @FXML
    private JFXTextField input_price;
    @FXML
    private JFXTextField selling_coeffitsient;
    @FXML
    private JFXTextField quantity;
    @FXML
    private JFXDatePicker createdDate;
    @FXML
    private JFXButton addBtn;
    @FXML
    private JFXTextField sale;
    @FXML
    private Label singlePoroductPrice;
    @FXML
    private Label wholeSalePrice;
    @FXML
    private Label wholeSaleSellingPrice;
    @FXML
    private Label overallProfit;
    @FXML
    private LineChart<String,Number> statistics;
    @FXML
    private BorderPane pane;
    @FXML
    private JFXButton backBtn;
    @FXML
    private JFXProgressBar updateProgressBar;
    @FXML
    private  Label progressLabel;
    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private ObservableList<Salesman> salesmanList = FXCollections.observableArrayList();
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
    private SalesRecords record;
    private Task<Void> updateTask;
    private TableView<SalesRecordsDto> tableView;


    public void setStage(Stage stage,Scene scene,Integer recordId,TableView tableView){
        this.stage = stage;
        this.scene = scene;
        this.recordId = recordId;
        this.tableView = tableView;

        this.stage.setOnCloseRequest(e->{
            if (updateTask != null){
                this.updateTask.cancel();
            }
        });

        fillForm();

        calculateSellingPrice();

        calculateWholeSalePrice();

        calculateOverallSellingPrice();

        calculateOverallProfit();
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
    private void min(MouseEvent event){
        stage.setIconified(true);
    }

    @FXML
    private void max(){
        if (!isFullScreen){
            stage.setFullScreenExitHint(" ");
            stage.setFullScreen(true);
            isFullScreen = true;
        }else {
            stage.setFullScreen(false);
            isFullScreen = false;
        }
    }

    @FXML
    private void close(){
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image image = new Image("image/no.jpg");
        record_image.setImage(image);
        record_image.setFitHeight(250);
        record_image.setFitWidth(250);
        this.updateProgressBar.setVisible(false);
        progressLabel.setVisible(false);

        upload_img.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG", "*.jpg", "*.JPEG", "*.jpeg"));

            File selectedImage = fileChooser.showOpenDialog(this.stage);

            try {
                DatabaseUtil.getSalesRecordsDao().uploadImageBySalesRecordId(selectedImage,recordId);

                SalesRecords temp = DatabaseUtil.getSalesRecordsDao().findSalesRecorById(recordId);

                Image img = new Image(new ByteArrayInputStream(temp.getImageBody()));

                record_image.setImage(img);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        pane.setStyle("-fx-effect: dropshadow(gaussian, black, 5, 0, 0, 0);" +
                "-fx-background-insets:5;");

        product_box.setEditable(true);
        salesman_box.setEditable(true);

        sale.setText(String.valueOf(0));

        selling_coeffitsient.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*")){
                showAllCalculations(newValue);

                calculateOverallSellingPrice();

                calculateOverallProfit();
            }
        });

        input_price.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*")){
                calculateWholeSalePrice();

                calculateOverallSellingPrice();

                calculateOverallProfit();
            }
        });

        sale.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*")){
                showAllCalculations(newValue);

                calculateOverallSellingPrice();

                calculateOverallProfit();
            }else {
                sale.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        quantity.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*")){
                showAllCalculations(newValue);

                calculateWholeSalePrice();

                calculateOverallProfit();
            }
        });

    }

    private void initChart(Integer productId){
        XYChart.Series<String,Number> series = new XYChart.Series<>();

        try {
            List<Statistics> statistics = DatabaseUtil.getSalesRecordsDao().findMonthlyExpenseById(productId);

            List<Statistics> sorted = statistics.stream()
                    .sorted(Comparator.comparing(Statistics::getDate))
                    .collect(Collectors.toList());

            System.out.println(sorted);

            for (Statistics st : sorted){
                String date = sdf.format(st.getDate());
                series.getData().add(new XYChart.Data<>(date, st.getSpentMoney()));
            }

            this.statistics.getData().add(series);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void backToMain(){
        this.stage.close();
    }

    private double calculateWholeSalePrice(){
        int quantity = 0;

        if (!this.quantity.getText().isEmpty()){
            quantity = Integer.parseInt(this.quantity.getText());

            if (quantity > 35000){
                return 0;
            }
        }

        double price =  this.input_price.getText().matches("[+-]?(\\d+|\\d+\\.\\d+|\\.\\d+|\\d+\\.)([eE]\\d+)?") ? Double.parseDouble(this.input_price.getText()) : 0;

        double overallCost = quantity * price;

        wholeSalePrice.setText(formatToCurrencyStandard(overallCost));

        return overallCost;
    }

    private void showAllCalculations(String newValue){
        if (newValue != null && !newValue.isEmpty()){
            calculateSellingPrice();
        }else {
            singlePoroductPrice.setText("_ _ _");
        }
    }

    private double calculateSellingPrice(){
        if (this.input_price.getText().isEmpty() || this.selling_coeffitsient.getText().isEmpty()
                ||this.sale.getText().isEmpty()){
            return 0;
        }
        double basePrice = Double.parseDouble(this.input_price.getText());
        double sale = Double.parseDouble(this.sale.getText());
        double coefficient = Double.parseDouble(this.selling_coeffitsient.getText());

        if ((sale < 0 || sale > 100) || (coefficient < 0)){
            singlePoroductPrice.setText("_ _ _");
        }else {
            double sellingPrice = basePrice * (((100 + coefficient) * (100 - sale)) / Math.pow(10,4));

            singlePoroductPrice.setText(formatToCurrencyStandard(sellingPrice));

            return sellingPrice;
        }
        return 0;
    }

    private double calculateOverallSellingPrice(){
        if (this.quantity.getText().isEmpty()){
            return 0;
        }

        int quantity = Integer.parseInt(this.quantity.getText());

        double sellingPrice = quantity * calculateSellingPrice();


        this.wholeSaleSellingPrice.setText(formatToCurrencyStandard(sellingPrice));

        return sellingPrice;
    }

    private void calculateOverallProfit(){
        if (this.input_price.getText().isEmpty() || this.selling_coeffitsient.getText().isEmpty()
                ||this.sale.getText().isEmpty()){
            return;
        }

        double overallPrice = calculateOverallSellingPrice();
        double overallCost = calculateWholeSalePrice();

        double profit = overallPrice - overallCost;

        this.overallProfit.setText(formatToCurrencyStandard(profit));
    }

    private String formatToCurrencyStandard(double sum){
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setGroupingSeparator(',');
        DecimalFormat formatter = new DecimalFormat("###,###,###.##",symbols);

        return formatter.format(sum);
    }

    private void fillForm(){
        if (recordId != null){
            try {
                record = DatabaseUtil.getSalesRecordsDao().findSalesRecorById(recordId);

                if (record != null){
                    productList.addAll(DatabaseUtil.getProductDao().findAllProducts());
                    product_box.setItems(productList);

                    salesmanList.addAll(DatabaseUtil.getSalesmanDao().findAllSalesmans());
                    salesman_box.setItems(salesmanList);

                    if (record.getProduct() != null){
                        initChart(record.getProduct().getId());

                        product_box.getSelectionModel().select(record.getProduct());
                    }

                    if (record.getSalesman() != null){
                        salesman_box.getSelectionModel().select(record.getSalesman());
                    }

                    byte[] imageBody = record.getImageBody();

                    if (imageBody != null){
                        Image image = new Image(new ByteArrayInputStream(record.getImageBody()));
                        record_image.setImage(image);
                    }

                    TextFields.bindAutoCompletion(product_box.getEditor(),product_box.getItems());
                    TextFields.bindAutoCompletion(salesman_box.getEditor(),salesman_box.getItems());

                    input_price.setText(String.valueOf(record.getInputPrice()));

                    double sellCoeff = record.getSellingCoefficient();
                    selling_coeffitsient.setText(String.valueOf(sellCoeff));

                    if (record.getDate() != null){
                        createdDate.setValue(DateTimeUtil.convertToLocalDate(record.getDate()));
                    }

                    quantity.setText(String.valueOf(record.getQuantity()));
                }else {
                    AlertUtil.showAlert(Alert.AlertType.WARNING,"Диққат","Диққат","Илтимос! Юқоридагиларни тўлиқ киритинг! ");
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    @FXML
    private void updateFully() throws Exception {
        this.updateProgressBar.setVisible(true);
        progressLabel.setVisible(true);

        updateTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                Platform.runLater(() -> {
                    try {
                        updateTask();

                        OptionalInt index = IntStream.range(0,tableView.getItems().size())
                                .filter(i -> recordId == tableView.getItems().get(i).getId())
                                .findFirst();

                        index.ifPresent(i -> tableView.getSelectionModel().select(i));

                        System.out.println(index + " : " + tableView.getItems().size());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                updateProgressBar.setVisible(false);
                progressLabel.setVisible(false);

                return null;
            }
        };

        Thread thread = new Thread(updateTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void updateTask() throws Exception {
        boolean isValidForm = true;
        int selectedProductIndex = product_box.getSelectionModel().getSelectedIndex();
        int selectedSaledmanIndex = salesman_box.getSelectionModel().getSelectedIndex();
        Product product = null;
        Salesman salesman = null;

        if (selectedProductIndex != -1){
            product = productList.get(selectedProductIndex);
        }else isValidForm = false;

        if (selectedSaledmanIndex != -1){
            salesman = salesmanList.get(selectedSaledmanIndex);
        }else isValidForm = false;

        if (record != null && product==null ){
            isValidForm = true;
            product = record.getProduct();
        }

        if (record != null && salesman == null){
            isValidForm = true;
            salesman = record.getSalesman();
        }

        if (product != null && salesman != null &&
                !input_price.getText().isEmpty() && !selling_coeffitsient.getText().isEmpty()
                && !quantity.getText().isEmpty() && !createdDate.getValue().toString().isEmpty()){
            double ownPrice = Double.parseDouble(input_price.getText());
            double sellCoeff = Double.parseDouble(selling_coeffitsient.getText());

            int quantity = Integer.parseInt(this.quantity.getText());
            Date date = DateTimeUtil.convertToDate(createdDate.getValue());

            BufferedImage bfi = SwingFXUtils.fromFXImage(record_image.getImage(),null);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            ImageIO.write(bfi,"jpg",bos);

            byte[] image = bos.toByteArray();

            bos.close();

            SalesRecords salesRecord = new SalesRecords();
            salesRecord.setId(recordId);
            salesRecord.setDate(date);
            salesRecord.setImageBody(image);
            salesRecord.setSellingCoefficient(sellCoeff);
            salesRecord.setQuantity(quantity);
            salesRecord.setProduct(product);
            salesRecord.setInputPrice(ownPrice);
            salesRecord.setSalesman(salesman);

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Ҳисоботни янгилаш");
            confirmation.setHeaderText("Тасдиқ!");
            confirmation.setContentText("Ушбу ҳисоботни янгилашга розимисиз?");

            confirmation.initModality(Modality.APPLICATION_MODAL);
            confirmation.initOwner(stage);

            ButtonType confirm = new ButtonType("Ҳа");
            ButtonType cancel = new ButtonType("Йўқ", ButtonBar.ButtonData.CANCEL_CLOSE);

            confirmation.getButtonTypes().setAll(confirm,cancel);

            Optional<ButtonType> result = confirmation.showAndWait();

            if (result.get() == confirm && isValidForm){
                DatabaseUtil.getSalesRecordsDao().updateSalesRecordFullyById(salesRecord);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Статус");
                alert.setHeaderText("Ҳисоботни янгилаш");
                alert.setContentText("Жорий ҳисобот мувафаққиятли янгиланди!");
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.initOwner(stage);
                alert.showAndWait();

                SalesRecordsController.getInstace().populateSalesRecordsTable();
                SalesRecordsController.getInstace().refreshFilter();
            }else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Диққат");
                alert.setHeaderText("Диққат");
                alert.setContentText("Илтимос! Юқоридагиларни тўлиқ киритинг! ");
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.initOwner(stage);
                alert.showAndWait();
            }
        }
    }
}
