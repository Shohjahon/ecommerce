package sample.model.dto;

import com.sun.org.apache.regexp.internal.RE;
import javafx.beans.property.*;
import sample.model.SalesRecords;
import sample.utility.DateTimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Shoh Jahon tomonidan 7/26/2019 da qo'shilgan.
 */
public class SalesRecordsDto {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty productName = new SimpleStringProperty();
    private DoubleProperty inputPrice = new SimpleDoubleProperty();
    private DoubleProperty outputPrice = new SimpleDoubleProperty();
    private StringProperty salesmanName = new SimpleStringProperty();
    private StringProperty productTypeName = new SimpleStringProperty();
    private ObjectProperty<LocalDateTime> date = new SimpleObjectProperty<>();

    public SalesRecordsDto() {
    }

    public LocalDateTime getDate() {
        return date.get();
    }


    public ObjectProperty<LocalDateTime> dateProperty() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date.set(date);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getProductName() {
        return productName.get();
    }

    public StringProperty productNameProperty() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName.set(productName);
    }

    public double getInputPrice() {
        return inputPrice.get();
    }

    public DoubleProperty inputPriceProperty() {
        return inputPrice;
    }

    public void setInputPrice(double inputPrice) {
        this.inputPrice.set(inputPrice);
    }

    public double getOutputPrice() {
        return outputPrice.get();
    }

    public DoubleProperty outputPriceProperty() {
        return outputPrice;
    }

    public void setOutputPrice(double outputPrice) {
        this.outputPrice.set(outputPrice);
    }

    public String getSalesmanName() {
        return salesmanName.get();
    }

    public StringProperty salesmanNameProperty() {
        return salesmanName;
    }

    public void setSalesmanName(String salesmanName) {
        this.salesmanName.set(salesmanName);
    }

    public String getProductTypeName() {
        return productTypeName.get();
    }

    public StringProperty productTypeNameProperty() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName.set(productTypeName);
    }

    public static SalesRecordsDto mapToSalesRecordsDto(SalesRecords salesRecords){
        SalesRecordsDto dto = new SalesRecordsDto();
        dto.setId(salesRecords.getId());
        dto.setProductName(salesRecords.getProduct().getProductName());
        dto.setProductTypeName(salesRecords.getProduct().getProductType().getProductType());
        dto.setSalesmanName(salesRecords.getSalesman().getFullName());
        dto.setOutputPrice(salesRecords.getOutputPrice());
        dto.setInputPrice(salesRecords.getInputPrice());
        dto.setDate(DateTimeUtil.convertToLocalDateTime(salesRecords.getDate()));

        return dto;
    }
}
