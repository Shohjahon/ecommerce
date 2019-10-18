package sample.utility;

import javafx.collections.ObservableList;
import javafx.event.Event;
import sample.model.SalesRecords;
import sample.model.dto.SalesRecordsDto;

/**
 * Shoh Jahon tomonidan 10/4/2019 da qo'shilgan.
 */
public class ValidationUtil {

    public static boolean isEmpty(SalesRecords records){
        if (records == null){
            return false;
        }else if (records.getProduct() == null){
            return false;
        }else if (records.getProduct().getProductType() == null){
            return false;
        }else if (records.getSalesman() == null){
            return false;
        }else if (records.getDate() == null){
            return false;
        }else if (records.getId() == 0){
            return false;
        }else if (records.getInputPrice() == null){
            return false;
        }else if (records.getOutputPrice() == null){
            return false;
        }

        return true;
    }

    public static void insertOrUpdate(SalesRecords salesRecords, ObservableList list){
        try {
            if (list.size() > 0){
                SalesRecordsDto dto = SalesRecordsDto.mapToSalesRecordsDto(salesRecords);

                    DatabaseUtil.getSalesRecordsDao().updateSalesRecord(salesRecords);

                    int index = indexById(dto,list);

                    if (index != -1){

                        dto.setOutputPrice(salesRecords.getInputPrice() * ((100 + salesRecords.getSellingCoefficient()) / 100));

                        list.set(index,dto);
                    }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }


    public static int indexById(SalesRecordsDto dto, ObservableList list){

        if (dto != null && list.size() > 0){
            for (int i = 0; i < list.size(); i++) {
                if (dto.getId() == ((SalesRecordsDto) list.get(i)).getId()){
                    return i;
                }
            }
        }

        return  -1;
    }
}
