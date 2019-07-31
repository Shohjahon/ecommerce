package sample.utility;

import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableView;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Shoh Jahon tomonidan 7/31/2019 da qo'shilgan.
 */
public class FilterUtil {
    private JFXTextField filterField;
    private TableView tableView;
    private ObservableList list;

    public FilterUtil(JFXTextField filterField,
                      TableView tableView,
                      ObservableList list){
        this.filterField = filterField;
        this.tableView = tableView;
        this.list = list;
    }

    public void initFilter(){
        FilteredList filterData = new FilteredList<>(list, f->true);

        System.out.println("init filter list: " + list);

        filterField.textProperty().addListener((observable, oldValue, newValue)->{
            filterData.setPredicate(dto -> {
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
                            System.out.println("init filter dto field name: " + value +"\nnewValue: "+newValue);
                            if (field.getName().equals("date")){
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
        String dateString = date.substring(23, date.length()-7);
        System.out.println("date string : " + dateString);
        String[] arr = dateString.split("-");
        String input = arr[2] + "." + arr[1] + "." + arr[0];
        System.out.println("input date: " + input);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        Date d;
        try {
            d = df.parse(input);
            String result = df.format(d);
            System.out.println("parsed date : " + result);
            return  result;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
