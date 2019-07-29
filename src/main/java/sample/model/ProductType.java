package sample.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Shoh Jahon on 3/31/2019.
 */
public class ProductType {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty productType = new SimpleStringProperty();

    public ProductType(String productType){
        this.productType.set(productType);
    }

    public ProductType() {
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

    public String getProductType() {
        return productType.get();
    }

    public StringProperty productTypeProperty() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType.set(productType);
    }

    @Override
    public String toString() {
        return productType.getValue();
    }
}
