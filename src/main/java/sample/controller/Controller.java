package sample.controller;

import animatefx.animation.FadeIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import sample.Main;
import sample.inteface.DispatcherController;
import sample.utility.AlertUtil;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Controller  implements Initializable{
    private Stage stage;
    private Scene scene;
    private double dragAnchorY;
    private double dragAnchorX;
    private boolean isFullScreen = false;
    @FXML
    private BorderPane borderPane;
    @FXML
    private BorderPane container;
    @FXML
    private JFXButton product_btn;
    @FXML
    private JFXButton prod_type_btn;
    @FXML
    private JFXButton salesman_btn;
    @FXML
    private JFXButton attachment_btn;
    @FXML
    private JFXTextField filterField;
    @FXML
    private JFXButton add_btn;
    @FXML
    private JFXButton delete_btn;
    @FXML
    private JFXButton brief_btn;
    @FXML
    private JFXButton toExcelBtn;
    private List<JFXButton> btnList;
    private String selectedView;
    private DispatcherController dispatcherController;

    private final Logger logger = Logger.getLogger(getClass());


    public void setStage(Stage stage, Scene scene){
        this.stage = stage;
        this.scene = scene;
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

    @FXML
    private void min(MouseEvent event){
        stage.setIconified(true);
    }


    @FXML
    public void loadProducts(){
        setDefaultColor();
        delete_btn.setVisible(false);
        brief_btn.setVisible(false);
        add_btn.setVisible(true);
        add_btn.setPrefWidth(157);
        selectedView = "product";
        this.product_btn.setStyle("-fx-background-color: #36A5BE;");
        loadUI("product");
    }

    @FXML
    private void loadDetailed(){
        setDefaultColor();
        delete_btn.setVisible(true);
        brief_btn.setVisible(true);
        add_btn.setVisible(false);
        add_btn.setPrefWidth(0);
        selectedView = "sales_record";
        this.attachment_btn.setStyle("-fx-background-color: #36A5BE;");
        loadUI("sales_record");
    }

    @FXML
    private void loadSalesman(){
        setDefaultColor();
        delete_btn.setVisible(false);
        brief_btn.setVisible(false);
        add_btn.setVisible(true);
        add_btn.setPrefWidth(157);
        selectedView = "salesman";
        this.salesman_btn.setStyle("-fx-background-color: #36A5BE;");
        loadUI("salesman");
    }

    @FXML
    private void loadProductType(){
        setDefaultColor();
        delete_btn.setVisible(false);
        brief_btn.setVisible(false);
        add_btn.setVisible(true);
        add_btn.setPrefWidth(157);
        selectedView = "product_type";
        this.prod_type_btn.setStyle("-fx-background-color: #36A5BE;");
        loadUI("product_type");
    }

    private void setDefaultColor(){
        if (btnList != null && btnList.size() > 0){
            for (int i = 0; i < btnList.size() ; i++) {
                String style = btnList.get(i).getStyle();
                if (style.contains("#36A5BE")){
                    btnList.get(i).setStyle("-fx-background-color:  #277688;");
                }
            }
        }
    }

    private void loadUI(String ui){
        Parent root = null;
        try {
            logger.debug("----> "+Main.class.getResource("/fxml/" + ui + ".fxml").toString());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + ui + ".fxml"));
            root = loader.load();
            switch (ui){
                case "product_type": selectedView = "create_product_type"; break;
                case "product": selectedView = "create_product"; break;
                case "salesman": selectedView = "create_salesman"; break;
                case "sales_record": selectedView = "create_sales_record"; break;
            }
            dispatcherController = loader.getController();
            dispatcherController.setStage(stage);
            dispatcherController.setFilterField(filterField);
            dispatcherController.setMainBtns(toExcelBtn,delete_btn,brief_btn);
        } catch (IOException e) {
           e.printStackTrace();
        }
        container.setCenter(root);
    }

    @FXML
    private void max(){
       if (!isFullScreen){
           stage.setFullScreenExitHint(" ");
           new FadeIn(stage.getScene().getRoot()).play();
           stage.setFullScreen(true);
           isFullScreen = true;
       }else {
           stage.setFullScreen(false);
           isFullScreen = false;
       }
    }


    @FXML
    private void close(){
        Platform.exit();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedView = "product";
        loadProducts();
        delete_btn.setVisible(false);
        btnList = Arrays.asList(product_btn,prod_type_btn,salesman_btn,attachment_btn);

        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;

        borderPane.setPrefSize(width - 150,height-100);
    }


    @FXML
    public void handleCreates() {
        showCreate(selectedView);
    }


    private void showCreate(String createUi){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/create/"+createUi+".fxml"));
        try {
                Parent root = loader.load();
                dispatcherController = loader.getController();
                Scene scene = null;
                FadeTransition ft = new FadeTransition(Duration.millis(800),root);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                ft.play();
                switch (createUi){
                    case "create_product": scene = new Scene(root, 600, 500); break;
                    case "create_product_type": scene = new Scene(root, 600, 400);break;
                    case "create_salesman": scene = new Scene(root,600,500); break;
                    case "create_sales_record": scene = new Scene(root,600,700); break;
                }


                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                StageStyle style = StageStyle.TRANSPARENT;
                stage.initStyle(style);
                dispatcherController.setStage(stage);
                stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showAlert(Alert.AlertType.ERROR,
                    "Хатолик",
                    "Хатолик юз берди! ",
                    "Дастур билан боғлиқ хатолик юз берди!  \n" +
                            "Илтимос дастур администратори билан боғланинг! ");
        }
    }
}
