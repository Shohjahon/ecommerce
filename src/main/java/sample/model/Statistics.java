package sample.model;

import java.util.Date;

/**
 * Shoh Jahon tomonidan 10/11/2019 da qo'shilgan.
 */
public class Statistics {
    private Date date;
    private Double spentMoney;

    public Statistics(Date date, Double spentMoney) {
        this.date = date;
        this.spentMoney = spentMoney;
    }

    public Statistics() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getSpentMoney() {
        return spentMoney;
    }

    public void setSpentMoney(Double spentMoney) {
        this.spentMoney = spentMoney;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "date=" + date +
                ", spentMoney=" + spentMoney +
                '}';
    }
}
