package sample.dao.impl;

import org.apache.log4j.Logger;
import sample.dao.ProductDao;
import sample.dao.SalesRecordsDao;
import sample.dao.SalesmanDao;
import sample.model.SalesRecords;
import sample.model.Statistics;
import sample.utility.DatabaseUtil;
import sample.utility.DateTimeUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by Shoh Jahon on 3/31/2019.
 */
public class SalesRecordsDaoImpl implements SalesRecordsDao {
    private final Connection connection;
    private ProductDao productDao;
    private SalesmanDao salesmanDao;
    private final String INSERT_SQL = "INSERT INTO sales_records (id_product,id_salesman,input_price,sell_coefficent,sold_date) VALUES (?,?,?,?,?)";
    private final String UPDATE_SQL = "UPDATE sales_records SET  id_product=?,id_salesman=?,input_price=?,sell_coefficent=?,sold_date=? WHERE id = ?";
    private final String DELETE_SQL = "DELETE FROM sales_records WHERE id = ?";
    private final String SELECT_ONE_SQL = "SELECT * FROM sales_records WHERE id = ?";
    private final String SELECT_ALL_SQL = "SELECT * FROM sales_records ORDER BY sold_date ASC ";
    private final String UPDATE_FULLY = "UPDATE sales_records SET id_product=?,id_salesman=?,input_price=?, sell_coefficent=?,sold_date=?,image_body=?,quantity=? WHERE  id=?";
    private final String UPDATE_IMAGE = "UPDATE sales_records SET image_body=? WHERE id=?";

    private Logger logger = Logger.getLogger(getClass());

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
                    salesRecords.setQuantity(rs.getInt("quantity"));
                    salesRecords.setSellingCoefficient(rs.getDouble("sell_coefficent"));
                    salesRecords.setDate(rs.getDate("sold_date"));
                    salesRecords.setImageBody(rs.getBytes("image_body"));
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
    public int insertSalesRecord(SalesRecords salesRecord) throws Exception {
        logger.info("insertSalesRecord() -> salesRecord: " + salesRecord.toString());

        try(PreparedStatement statement = connection.prepareStatement(INSERT_SQL,
                Statement.RETURN_GENERATED_KEYS)) {

            connection.setAutoCommit(false);
            if (salesRecord.getId() == 0){
                statement.setInt(1,-1);
                statement.setInt(2,-1);
                statement.setDouble(3,0);
                statement.setDouble(4,30);
                statement.setDate(5, new java.sql.Date(salesRecord.getDate().getTime()));
            }else {
                statement.setInt(1,salesRecord.getProduct().getId());
                statement.setInt(2,salesRecord.getSalesman().getId());
                statement.setDouble(3,salesRecord.getInputPrice());
                statement.setDouble(4,salesRecord.getSellingCoefficient());
                statement.setDate(5, new java.sql.Date(salesRecord.getDate().getTime()));
            }
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()){
                if (generatedKeys.next()){
                    return generatedKeys.getInt(1);
                }
            }
            connection.commit();
        }catch (SQLException ex){
            connection.rollback();
            throw new Exception(ex.getMessage());
        }finally {
            connection.setAutoCommit(true);
        }
        return 0;
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

            if (salesRecord.getSalesman() == null){
                statement.setInt(2,-1);
            }else {
                statement.setInt(2,salesRecord.getSalesman().getId());
            }

            if (salesRecord.getProduct() == null){
                statement.setInt(1,-1);
            }else {
                statement.setInt(1,salesRecord.getProduct().getId());
            }
            statement.setDouble(3,salesRecord.getInputPrice());
            statement.setDouble(4,salesRecord.getSellingCoefficient());
            if (salesRecord.getDate() != null){
                statement.setDate(5,new java.sql.Date(salesRecord.getDate().getTime()));
            }else {
                statement.setDate(5,new java.sql.Date(new java.util.Date().getTime()));
            }
            statement.setInt(6,salesRecord.getId());

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
                    records.setDate(rs.getDate("sold_date"));
                    records.setQuantity(rs.getInt("quantity"));
                    records.setSellingCoefficient(rs.getDouble("sell_coefficent"));
                    records.setImageBody(rs.getBytes("image_body"));
                    list.add(records);
                }
            }
        }catch (SQLException ex){
            throw new Exception(ex.getMessage());
        }finally {
            return list;
        }
    }

    @Override
    public void updateSalesRecordFullyById(SalesRecords salesRecord) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_FULLY)){
            connection.setAutoCommit(false);

            if (salesRecord.getSalesman() == null){
                statement.setInt(2,-1);
            }else {
                statement.setInt(2,salesRecord.getSalesman().getId());
            }

            if (salesRecord.getProduct() == null){
                statement.setInt(1,-1);
            }else {
                statement.setInt(1,salesRecord.getProduct().getId());
            }

            statement.setDouble(3,salesRecord.getInputPrice());
            statement.setDouble(4,salesRecord.getSellingCoefficient());
            statement.setDate(5,new java.sql.Date(salesRecord.getDate().getTime()));
            statement.setBytes(6,salesRecord.getImageBody());
            statement.setDouble(7,salesRecord.getSellingCoefficient());
            statement.setInt(8,salesRecord.getId());

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
    public void uploadImageBySalesRecordId(File file, Integer id) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_IMAGE)){
            connection.setAutoCommit(false);

            if (file.exists()){
                BufferedImage bufferedImage = ImageIO.read(file);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage,"jpg",bos);

                byte[] image = bos.toByteArray();

                statement.setBytes(1,image);
                statement.setInt(2,id);

                statement.executeUpdate();
            }

            connection.commit();
        }catch (SQLException ex){
            connection.rollback();
            ex.printStackTrace();
        }finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public List<Statistics> findMonthlyExpenseById(Integer id) throws Exception {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        calendar.add(Calendar.DAY_OF_MONTH,-31);
        java.util.Date start = calendar.getTime();
        System.out.println(sdf.format(start));
        List<Statistics> statistics = new ArrayList<>();

        LocalDate monthBefore = DateTimeUtil.convertToLocalDate(calendar.getTime());
        LocalDate now = LocalDate.now();

        List<SalesRecords> salesRecords = findAllSalesRecords();

        salesRecords.forEach(salesRecord -> {


            if (salesRecord.getDate()!= null && salesRecord.getProduct() != null){
                LocalDate recordDate = DateTimeUtil.convertToLocalDate(salesRecord.getDate());

                if (recordDate.isAfter(monthBefore) && recordDate.isBefore(now) && id == salesRecord.getProduct().getId()){
                    Statistics statistic = new Statistics();
                    statistic.setSpentMoney(salesRecord.getQuantity() * salesRecord.getInputPrice());
                    statistic.setDate(salesRecord.getDate());

                    statistics.add(statistic);
                }
            }

        });

        return statistics;
    }
}
