package sample.dao;

import sample.model.SalesRecords;

import java.util.List;

/**
 *  Shoh Jahon tomonidan 3/31/2019 da qo'shilgan.
 */
public interface SalesRecordsDao {

    SalesRecords findSalesRecorById(Integer id) throws Exception;

    void insertSalesRecord(SalesRecords salesRecord) throws Exception;

    void deleteSalesRecord(Integer id) throws Exception;

    void updateSalesRecord(SalesRecords salesRecord) throws Exception;

    List<SalesRecords> findAllSalesRecords() throws Exception;
}
