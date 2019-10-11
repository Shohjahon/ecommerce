package sample.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
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
public class CreateSalesmanController implements Initializable,DispatcherController<SalesmanDto> {
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
    private SalesmanController salesmanController;

    @FXML
    public void createSalesman(){
        String fullName = salesmanName.getText();
        String phoneNumber = salesmanPhone.getText();
        String address = salesmanAddress.getText();

        boolean isValid = !(fullName.isEmpty() || phoneNumber.isEmpty() || address.isEmpty());

        if (isValid){
            Salesman salesman = new Salesman();
            salesman.setFullName(fullName);
            salesman.setAddress(address);
            salesman.setPhoneNumber(phoneNumber);
            try {
                salesmanDao.insertSalesman(salesman);
                salesmanController = SalesmanController.getInstance();
                salesmanController.populateSalesmanTable();
                handleCancel();
                salesmanController.refreshFilter();
            } catch (Exception e) {
                e.printStackTrace();
                AlertUtil.showAlert(Alert.AlertType.ERROR,
                        "Хатолик",
                        "Хатолик юз берди! ",
                        "Дастур билан боғлиқ хатолик юз берди!  \n" +
                                "Илтимос дастур администратори билан боғланинг! ");
            }
        }else{
            AlertUtil.showAlert(Alert.AlertType.WARNING,
                    "Диққат!",
                    "Илтимос киритилган маълумотни текширинг!",
                    "Илтимос!  Киритилган маълумот бўш сатр эмаслигини\n" +
                            "ва уни тўғри киритилганлигига текширинг.");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        salesmanDao = DatabaseUtil.getSalesmanDao();
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setData(ObservableList<SalesmanDto> list, SalesmanDto dto, int index) {

    }

    @Override
    public void setFilterField(JFXTextField filterField) {

    }

    @Override
    public void setMainBtns(JFXButton export, JFXButton deleteBtn,JFXButton brief_btn) {

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
