package sample.model;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by Shoh Jahon on 3/31/2019.
 */
public class SalesRecords {
    private Integer id;
    private Product product;
    private Salesman salesman;
    private Double inputPrice;
    private Double outputPrice;
    private Date date;
    private byte[] imageBody;
    private Integer quantity;
    private Double sellingCoefficient;


    public SalesRecords() {
    }

    public byte[] getImageBody() {
        return imageBody;
    }

    public void setImageBody(byte[] imageBody) {
        this.imageBody = imageBody;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getSellingCoefficient() {
        return sellingCoefficient;
    }

    public void setSellingCoefficient(Double sellingCoefficient) {
        this.sellingCoefficient = sellingCoefficient;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Salesman getSalesman() {
        return salesman;
    }

    public void setSalesman(Salesman salesman) {
        this.salesman = salesman;
    }

    public Double getInputPrice() {
        return inputPrice;
    }

    public void setInputPrice(Double inputPrice) {
        this.inputPrice = inputPrice;
    }

    public Double getOutputPrice() {
        return outputPrice;
    }

    public void setOutputPrice(Double outputPrice) {
        this.outputPrice = outputPrice;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    @Override
    public String toString() {
        return "SalesRecords{" +
                "id=" + id +
                ", product=" + product +
                ", salesman=" + salesman +
                ", inputPrice=" + inputPrice +
                ", outputPrice=" + outputPrice +
                ", date=" + date +
                ", imageBody=" + Arrays.toString(imageBody) +
                ", quantity=" + quantity +
                ", sellingCoefficient=" + sellingCoefficient +
                '}';
    }
}
