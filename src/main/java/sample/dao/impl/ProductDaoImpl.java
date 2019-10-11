package sample.dao.impl;

import javafx.beans.property.IntegerProperty;
import sample.dao.ProductDao;
import sample.dao.ProductTypeDao;
import sample.model.Product;
import sample.model.ProductType;
import sample.utility.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shoh Jahon on 3/31/2019.
 */
public class ProductDaoImpl implements ProductDao{
    private final Connection connection;
    private ProductTypeDao productTypeDao;
    private final String INSERT_SQL = "INSERT INTO products (product_name,id_product_type, description) VALUES (?,?,?)";
    private final String UPDATE_SQL = "UPDATE products SET  product_name = ?, id_product_type = ?, description = ? WHERE id = ?";
    private final String DELETE_SQL = "DELETE FROM products WHERE id = ?";
    private final String SELECT_ONE_SQL = "SELECT * FROM products WHERE id = ?";
    private final String SELECT_ALL_SQL = "SELECT * FROM products";

    public ProductDaoImpl(Connection connection) {
        this.connection = connection;
        productTypeDao = DatabaseUtil.getProductTypeDao();
    }

    @Override
    public Product findProductById(Integer id) throws Exception {
        Product product = null;
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ONE_SQL)) {
            connection.setAutoCommit(false);
            statement.setInt(1, id);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    product = new Product(result.getString("product_name"));
                    product.setProductType(productTypeDao.findProductTypeById(result.getInt("id_product_type")));
                    product.setId(result.getInt("id"));
                    product.setDescription(result.getString("description"));
                }
            }
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw new Exception(ex.getMessage());
        } finally {
            connection.setAutoCommit(true);
            return product;
        }
    }

    @Override
    public void deleteProduct(Integer id) throws Exception {
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
    public void insertProduct(Product product) throws Exception {
        try(PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            connection.setAutoCommit(false);
            statement.setString(1,product.getProductName());
            statement.setInt(2,product.getProductType().getId());
            statement.setString(3,product.getDescription());
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
    public void updateProduct(Product product) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)){
            connection.setAutoCommit(false);
            statement.setString(1,product.getProductName());
            statement.setInt(2,product.getProductType().getId());
            statement.setString(3,product.getDescription());
            statement.setInt(4,product.getId());
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
    public List<Product> findAllProducts() throws Exception {
        List<Product> list = new ArrayList<>();
        try (Statement statement = connection.createStatement()){
            try (ResultSet result = statement.executeQuery(SELECT_ALL_SQL)){
                while (result.next()){
                    Product product = new Product();
                    product.setId(result.getInt("id"));
                    product.setProductName(result.getString("product_name"));
                    product.setDescription(result.getString("description"));
                    Integer pId = result.getInt("id_product_type");
                    ProductType productType = productTypeDao.findProductTypeById(pId);
                    product.setProductType(productTypeDao.findProductTypeById(result.getInt("id_product_type")));
                    list.add(product);
                }
            }
        }catch (SQLException ex){
            throw new Exception(ex.getMessage());
        }finally {
            return list;
        }
    }

}
