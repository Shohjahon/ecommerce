package sample.model;

/**
 * Created by Shoh Jahon on 3/31/2019.
 */
public class Product {
    private Integer id;
    private String productName;
    private ProductType productType;

    public Product(String productName) {
        this.productName = productName;
    }

    public Product() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }
}
