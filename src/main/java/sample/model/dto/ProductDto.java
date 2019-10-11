package sample.model.dto;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import sample.model.Product;
import sample.model.Salesman;

/**
 * Shoh Jahon tomonidan 7/22/2019 da qo'shilgan.
 */
public class ProductDto {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty productName = new SimpleStringProperty();
    private StringProperty productType = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();

    public ProductDto() {
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

    public String getProductType() {
        return productType.get();
    }

    public StringProperty productTypeProperty() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType.set(productType);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public static ProductDto mapToProductDto(Product product){
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setProductName(product.getProductName());
        productDto.setProductType(product.getProductType().getProductType());
        productDto.setDescription(product.getDescription());
        return productDto;
    }

    @Override
    public String toString() {
        return "ProductDto{" +
                "id=" + id +
                ", productName=" + productName +
                ", productType=" + productType +
                ", description=" + description +
                '}';
    }
}
