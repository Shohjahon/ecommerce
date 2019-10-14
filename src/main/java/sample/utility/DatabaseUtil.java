package sample.utility;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import sample.constant.Query;
import sample.dao.ProductDao;
import sample.dao.ProductTypeDao;
import sample.dao.SalesRecordsDao;
import sample.dao.SalesmanDao;
import sample.dao.impl.ProductDaoImpl;
import sample.dao.impl.ProductTypeDaoImpl;
import sample.dao.impl.SalesRecordsDaoImpl;
import sample.dao.impl.SalesmanDaoImpl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Shoh Jahon on 3/31/2019.
 */
public class DatabaseUtil {

    private static Connection connection;
    private static ProductTypeDao productTypeDao;
    private static ProductDao productDao;
    private static SalesmanDao salesmanDao;
    private static SalesRecordsDao salesRecordsDao;

    public static Connection getConnection(){
        if (connection == null){
            try {
                String url;
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection("jdbc:sqlite:ecommerce.db");
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);

                statement.executeUpdate(Query.create_salesman);
                statement.executeUpdate(Query.create_product_type);
                statement.executeUpdate(Query.create_product);
                statement.executeUpdate(Query.create_sales_record);
            } catch (SQLException e) {
                Logger.getLogger(DatabaseUtil.class.getName()).log(Level.SEVERE,null,e);
            } catch (ClassNotFoundException e) {
                Logger.getLogger(DatabaseUtil.class.getName()).log(Level.SEVERE,null,e);
            }
        }
        return connection;
    }

    public static ProductTypeDao getProductTypeDao(){
        if (productTypeDao == null){
            productTypeDao = new ProductTypeDaoImpl(getConnection());
        }
        return productTypeDao;
    }

    public static ProductDao getProductDao(){
        if (productDao == null){
            productDao = new ProductDaoImpl(getConnection());
        }
        return productDao;
    }

    public static SalesmanDao getSalesmanDao(){
        if (salesmanDao == null){
            salesmanDao = new SalesmanDaoImpl(getConnection());
        }
        return salesmanDao;
    }

    public static SalesRecordsDao getSalesRecordsDao(){
        if (salesRecordsDao == null){
            salesRecordsDao = new SalesRecordsDaoImpl(getConnection());
        }
        return salesRecordsDao;
    }

}
