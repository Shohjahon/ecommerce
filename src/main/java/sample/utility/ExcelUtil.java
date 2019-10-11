package sample.utility;

import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Shoh Jahon tomonidan 7/31/2019 da qo'shilgan.
 */
public class ExcelUtil<T> {
    private TableView<T> tableView;
    private String spreedSheetName;
    private Stage stage;

    public ExcelUtil(TableView<T> tableView, String spreedSheetName, Stage stage) {
        this.tableView = tableView;
        this.spreedSheetName = spreedSheetName;
        this.stage = stage;
    }

    public void exportToExcel(){
        Workbook workbook = new HSSFWorkbook();
        Sheet spreedsheet = workbook.createSheet(spreedSheetName);
        Row row = spreedsheet.createRow(0);

        for (int i = 0; i < tableView.getColumns().size()-1; i++) {
            row.createCell(i).setCellValue(tableView.getColumns().get(i).getText());
            spreedsheet.autoSizeColumn(i);
        }

        for (int i = 0; i < tableView.getItems().size(); i++) {
            row = spreedsheet.createRow(i+1);
            for (int j = 0; j < tableView.getColumns().size()-1; j++) {
                if (tableView.getColumns().get(j).getCellData(i)!=null){
                    row.createCell(j).setCellValue(tableView.getColumns().get(j).getCellData(i).toString());
                }else {
                    row.createCell(j).setCellValue("");
                }
            }
        }

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("excel files (*.xls)", "*.xls");
        fileChooser.getExtensionFilters().add(filter);

        File file = fileChooser.showSaveDialog(stage);

        if (file!=null){
            try (FileOutputStream outputStream = new FileOutputStream(file.getAbsolutePath())){
                workbook.write(outputStream);

                AlertUtil.showAlert(Alert.AlertType.INFORMATION,"Хабар","Маълумотни ўтказиш","Статистикага оид маълумотлар эхcелга мувафаққиятли  ўтказилди!");

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
