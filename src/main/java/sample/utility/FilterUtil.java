package sample.utility;

import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Shoh Jahon tomonidan 7/31/2019 da qo'shilgan.
 */
public class FilterUtil {
    private JFXTextField filterField;
    private TableView tableView;
    private ObservableList list;
    private Set<Integer> selectedRows;

    public FilterUtil(JFXTextField filterField,
                      TableView tableView,
                      ObservableList list){
        this.filterField = filterField;
        this.tableView = tableView;
        this.list = list;
    }

    public void setSelectedRowsAndClear(Set<Integer> selectedRows){
        this.selectedRows = selectedRows;
    }

    public void initFilter(){
        FilteredList filterData = new FilteredList<>(list, f->true);

        filterField.textProperty().addListener((observable, oldValue, newValue)->{
            filterData.setPredicate(dto -> {
                if (selectedRows != null){
                    selectedRows.clear();
                }
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                for (Field field : dto.getClass().getDeclaredFields()){
                    field.setAccessible(true);
                    try {
                        Object t = field.get(dto);

                        if (t != null){
                            String value = t.toString();
                            String fieldName = field.getName();
                            boolean isDate = fieldName.equals("date");

                            if (isDate && !value.contains("null")){
                                value = formatStringDate(value);
                                if (value.toLowerCase().contains(lowerCaseFilter)) return true;
                            }else if (value.toLowerCase().contains(lowerCaseFilter)){
                                return true;
                            }
                        }
                    } catch (IllegalAccessException e) {
                        System.out.println("Error in analyzing object");
                    }
                }
                return false;
            });
        });

        SortedList sortedData = new SortedList<>(filterData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(filterData);
    }

    private String formatStringDate(String date){
        String dateString = date.substring(23, date.length()-1);
        String[] arr = dateString.split("-");
        String input = arr[2] + "." + arr[1] + "." + arr[0];
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        Date d;
        try {
            d = df.parse(input);
            String result = df.format(d);
            return  result;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
