package sample.dao.impl;

import sample.dao.ProductDao;
import sample.dao.SalesRecordsDao;
import sample.dao.SalesmanDao;
import sample.model.SalesRecords;
import sample.utility.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shoh Jahon on 3/31/2019.
 */
public class SalesRecordsDaoImpl implements SalesRecordsDao {
    private final Connection connection;
    private ProductDao productDao;
    private SalesmanDao salesmanDao;
    private final String INSERT_SQL = "INSERT INTO sales_records (id_product,id_salesman,input_price,output_price) VALUES (?,?,?,?)";
    private final String UPDATE_SQL = "UPDATE sales_records SET  id_product=?,id_salesman=?,input_price=?,output_price=? WHERE id = ?";
    private final String DELETE_SQL = "DELETE FROM sales_records WHERE id = ?";
    private final String SELECT_ONE_SQL = "SELECT * FROM sales_records WHERE id = ?";
    private final String SELECT_ALL_SQL = "SELECT * FROM sales_records";

    public SalesRecordsDaoImpl(Connection connection) {
        this.connection = connection;
        productDao = DatabaseUtil.getProductDao();
        salesmanDao = DatabaseUtil.getSalesmanDao();
    }

    @Override
    public SalesRecords findSalesRecorById(Integer id) throws Exception {
        SalesRecords salesRecords = null;
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ONE_SQL)){
            connection.setAutoCommit(false);
            statement.setInt(1,id);
            try (ResultSet rs = statement.executeQuery()){
                if (rs.next()){
                    salesRecords = new SalesRecords();
                    salesRecords.setId(rs.getInt("id"));
                    salesRecords.setProduct(productDao.findProductById(rs.getInt("id_product")));
                    salesRecords.setSalesman(salesmanDao.findSalesmanById(rs.getInt("id_salesman")));
                    salesRecords.setInputPrice(rs.getDouble("input_price"));
                    salesRecords.setOutputPrice(rs.getDouble("output_price"));
                }
            }
            connection.commit();
        }catch (SQLException ex){
            connection.rollback();
            throw new Exception(ex.getMessage());
        }finally {
            connection.setAutoCommit(true);
            return salesRecords;
        }
    }

    @Override
    public void insertSalesRecord(SalesRecords salesRecord) throws Exception {
        try(PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            connection.setAutoCommit(false);
            statement.setInt(1,salesRecord.getProduct().getId());
            statement.setInt(2,salesRecord.getSalesman().getId());
            statement.setDouble(3,salesRecord.getInputPrice());
            statement.setDouble(4,salesRecord.getOutputPrice());
            statement.executeUpdate();
            connection.commit();
        }catch (SQLException ex){
            connection.rollback();
            throw new Exception(ex.getMessage());
        }finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void deleteSalesRecord(Integer id) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_SQL)){
            connection.setAutoCommit(false);
            statement.setInt(1,id);
            statement.executeUpdate();
            connection.commit();
        }catch (SQLException ex){
            connection.rollback();
            throw new Exception(ex.getMessage());
        }finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void updateSalesRecord(SalesRecords salesRecord) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)){
            connection.setAutoCommit(false);
            statement.setInt(1,salesRecord.getProduct().getId());
            statement.setInt(2,salesRecord.getSalesman().getId());
            statement.setDouble(3,salesRecord.getInputPrice());
            statement.setDouble(4,salesRecord.getOutputPrice());
            statement.setInt(5,salesRecord.getId());
            statement.executeUpdate();
            connection.commit();
        }catch (SQLException ex){
            connection.rollback();
            throw new Exception(ex.getMessage());
        }finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public List<SalesRecords> findAllSalesRecords() throws Exception {
        List<SalesRecords> list = new ArrayList<>();
        try (Statement statement = connection.createStatement()){
            try (ResultSet rs = statement.executeQuery(SELECT_ALL_SQL)){
                while (rs.next()){
                    SalesRecords records = new SalesRecords();
                    records.setId(rs.getInt("id"));
                    records.setProduct(productDao.findProductById(rs.getInt("id_product")));
                    records.setSalesman(salesmanDao.findSalesmanById(rs.getInt("id_salesman")));
                    records.setInputPrice(rs.getDouble("input_price"));
                    records.setOutputPrice(rs.getDouble("output_price"));
                    list.add(records);
                }
            }
        }catch (SQLException ex){
            throw new Exception(ex.getMessage());
        }finally {
            return list;
        }
    }
}
