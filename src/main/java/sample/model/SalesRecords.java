package sample.model;

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

    public SalesRecords() {
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
                '}';
    }
}
