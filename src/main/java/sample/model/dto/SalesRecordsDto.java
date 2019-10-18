package sample.model.dto;

import com.sun.org.apache.regexp.internal.RE;
import javafx.beans.property.*;
import sample.model.Product;
import sample.model.ProductType;
import sample.model.SalesRecords;
import sample.model.Salesman;
import sample.utility.DateTimeUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Shoh Jahon tomonidan 7/26/2019 da qo'shilgan.
 */
public class SalesRecordsDto {
    private IntegerProperty id = new SimpleIntegerProperty();
    private ObjectProperty<Product> productName = new SimpleObjectProperty<>();
    private DoubleProperty inputPrice = new SimpleDoubleProperty();
    private DoubleProperty outputPrice = new SimpleDoubleProperty();
    private ObjectProperty<Salesman> salesmanName = new SimpleObjectProperty<>();
    private ObjectProperty<ProductType> productTypeName = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();

    public SalesRecordsDto() {
    }

    public Product getProductName() {
        return productName.get();
    }

    public ObjectProperty<Product> productNameProperty() {
        return productName;
    }

    public void setProductName(Product productName) {
        this.productName.set(productName);
    }

    public Salesman getSalesmanName() {
        return salesmanName.get();
    }

    public ObjectProperty<Salesman> salesmanNameProperty() {
        return salesmanName;
    }

    public void setSalesmanName(Salesman salesmanName) {
        this.salesmanName.set(salesmanName);
    }

    public ProductType getProductTypeName() {
        return productTypeName.get();
    }

    public ObjectProperty<ProductType> productTypeNameProperty() {
        return productTypeName;
    }

    public void setProductTypeName(ProductType productTypeName) {
        this.productTypeName.set(productTypeName);
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


    public Double getInputPrice() {
        return inputPrice.get();
    }

    public DoubleProperty inputPriceProperty() {
        return inputPrice;
    }

    public void setInputPrice(Double inputPrice) {
        this.inputPrice.set(inputPrice);
    }

    public Double getOutputPrice() {
        return outputPrice.get();
    }

    public DoubleProperty outputPriceProperty() {
        return outputPrice;
    }

    public void setOutputPrice(Double outputPrice) {
        this.outputPrice.set(outputPrice);
    }

    public void setInputPrice(double inputPrice) {
        this.inputPrice.set(inputPrice);
    }

    public void setOutputPrice(double outputPrice) {
        this.outputPrice.set(outputPrice);
    }

    public LocalDate getDate() {
        return date.get();
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    public static SalesRecordsDto mapToSalesRecordsDto(SalesRecords salesRecords){
        SalesRecordsDto dto = new SalesRecordsDto();
        dto.setId(salesRecords.getId());
        if (salesRecords.getProduct() != null){
            dto.setProductName(salesRecords.getProduct());
            dto.setProductTypeName(salesRecords.getProduct().getProductType());
        }
        dto.setSalesmanName(salesRecords.getSalesman());
        dto.setOutputPrice(salesRecords.getOutputPrice());
        dto.setInputPrice(salesRecords.getInputPrice());
        if (salesRecords.getDate() != null){
            dto.setDate(DateTimeUtil.convertToLocalDate(salesRecords.getDate()));
        }else {
            dto.setDate(DateTimeUtil.convertToLocalDate(new Date()));
        }

        return dto;
    }

    public static SalesRecords mapToSalesRecords(SalesRecordsDto dto){
        SalesRecords salesRecords = new SalesRecords();

        salesRecords.setId(dto.getId());
        Product product = new Product();
        if (dto.getProductName() != null){
            product.setProductName(dto.getProductName().getProductName());
            product.setProductType(dto.getProductTypeName());
            product.setId(dto.getProductName().getId());
            product.setDescription(dto.getProductName().getDescription());

            salesRecords.setProduct(product);
        }
        salesRecords.setSalesman(dto.getSalesmanName());
        salesRecords.setInputPrice(dto.getInputPrice());
        salesRecords.setOutputPrice(dto.getOutputPrice());
        salesRecords.setDate(DateTimeUtil.convertToDate(dto.getDate()));

        return salesRecords;
    }
}
