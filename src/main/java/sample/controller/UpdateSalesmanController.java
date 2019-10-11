package sample.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import sample.dao.SalesmanDao;
import sample.inteface.DispatcherController;
import sample.model.Salesman;
import sample.model.dto.SalesmanDto;
import sample.utility.AlertUtil;
import sample.utility.DatabaseUtil;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Shoh Jahon tomonidan 7/26/2019 da qo'shilgan.
 */
public class UpdateSalesmanController implements Initializable,DispatcherController<SalesmanDto> {
    private Stage stage;
    @FXML
    private JFXButton cancelBtn;
    @FXML
    private JFXButton addBtn;
    private double dragAnchorY;
    private double dragAnchorX;
    @FXML
    private JFXTextField salesmanName;
    @FXML
    private JFXTextField salesmanPhone;
    @FXML
    private JFXTextField salesmanAddress;
    private SalesmanDao salesmanDao;
    private ObservableList<SalesmanDto> salesmanDtos;
    private SalesmanDto salesman;
    private int index;

    @FXML
    public void updateSalesman(){
        String fullName = salesmanName.getText();
        String phoneNumber = salesmanPhone.getText();
        String address = salesmanAddress.getText();

        boolean isValid = !(fullName.isEmpty() || phoneNumber.isEmpty() || address.isEmpty());

        if (isValid){
            Salesman salesman = new Salesman();
            salesman.setId(this.salesman.getId());
            salesman.setFullName(fullName);
            salesman.setPhoneNumber(phoneNumber);
            salesman.setAddress(address);

            try {
                salesmanDao.updateSalesman(salesman);
                salesmanDtos.set(index,SalesmanDto.toSalesmanDto(salesman));
                handleCancel();
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtil.showAlert(Alert.AlertType.ERROR,
                        "Хатолик",
                        "Хатолик юз берди! ",
                        "Дастур билан боғлиқ хатолик юз берди!  \n" +
                                "Илтимос дастур администратори билан боғланинг! ");
            }
        }else {
            AlertUtil.showAlert(Alert.AlertType.WARNING,
                    "Диққат!",
                    "Илтимос киритилган маълумотни текширинг!",
                    "Илтимос!  Киритилган маълумот бўш сатр эмаслигини\n" +
                            "ва уни тўғри киритилганлигига текширинг.");
        }
    }

    public void setData(ObservableList<SalesmanDto> dtos,SalesmanDto salesmanDto,int index){
        this.salesmanDtos = dtos;
        this.salesman = salesmanDto;
        this.index = index;

        salesmanName.setText(salesmanDto.getFullName());
        salesmanPhone.setText(salesmanDto.getPhoneNumber());
        salesmanAddress.setText(salesmanDto.getAddress());
    }

    @Override
    public void setFilterField(JFXTextField filterField) {

    }

    @Override
    public void setMainBtns(JFXButton export, JFXButton deleteBtn,JFXButton brief_btn) {

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        salesmanDao = DatabaseUtil.getSalesmanDao();
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void handleCancel(){
        stage.close();

    }

    @FXML
    public void onMousePressed(MouseEvent me) {
        dragAnchorY = me.getScreenY() - stage.getY();
        dragAnchorX = me.getScreenX() - stage.getX();
    }
    @FXML
    public void onMouseDragged(MouseEvent me) {
        stage.setX(me.getScreenX() - dragAnchorX);
        stage.setY(me.getScreenY() - dragAnchorY);
    }
}
