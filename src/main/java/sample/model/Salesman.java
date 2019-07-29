package sample.model;

/**
 * Created by Shoh Jahon on 3/31/2019.
 */
public class Salesman {
    private Integer id;
    private String fullName;
    private String phoneNumber;
    private String address;

    public Salesman(String fullName, String phoneNumber) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    public Salesman() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return fullName;
    }
}
