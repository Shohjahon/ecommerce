package sample.model.dto;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import sample.model.Salesman;

/**
 * Shoh Jahon tomonidan 7/26/2019 da qo'shilgan.
 */
public class SalesmanDto {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty fullName = new SimpleStringProperty();
    private StringProperty phoneNumber = new SimpleStringProperty();
    private StringProperty address = new SimpleStringProperty();

    public SalesmanDto() {
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

    public String getFullName() {
        return fullName.get();
    }

    public StringProperty fullNameProperty() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName.set(fullName);
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public String getAddress() {
        return address.get();
    }

    public StringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    @Override
    public String toString() {
        return "SalesmanDto{" +
                "id=" + id.get() +
                ", fullName=" + fullName.get() +
                ", phoneNumber=" + phoneNumber.get() +
                ", address=" + address.get() +
                '}';
    }

    public static SalesmanDto toSalesmanDto(Salesman salesman){
        SalesmanDto dto = new SalesmanDto();
        dto.setFullName(salesman.getFullName());
        dto.setPhoneNumber(salesman.getPhoneNumber());
        dto.setAddress(salesman.getAddress());

        return dto;
    }
}
