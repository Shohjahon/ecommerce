package sample.utility;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Shoh Jahon tomonidan 7/18/2019 da qo'shilgan.
 */
public class AlertUtil {
    private static Alert alert;

    public static void showAlert(Alert.AlertType type,
                          String title,
                          String header,
                          String content){
        alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static boolean showConfirmBox(Alert.AlertType type,
                                         String title,
                                         String header,
                                         String content){
        alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        ButtonType confirm = new ButtonType("Ҳа");
        ButtonType cancel = new ButtonType("Йўқ", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(confirm,cancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == confirm){
            return true;
        }
        return false;
    }
}
