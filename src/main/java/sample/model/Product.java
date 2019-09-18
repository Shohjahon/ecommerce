package sample.model;

/**
 * Created by Shoh Jahon on 3/31/2019.
 */
public class Product {
    private Integer id;
    private String productName;
    private ProductType productType;
    private String description;

    public Product(String productName) {
        this.productName = productName;
    }


    public Product() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String printProduct(){
        String pr = "{\n" +getProductName()
                +"\n" + getProductType().getProductType()
                +"\n"+getDescription() + "}";
        return pr;
    }

    @Override
    public String toString() {
        return productName;
    }
}
