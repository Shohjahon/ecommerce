package sample.dao.impl;

import sample.dao.ProductTypeDao;
import sample.model.ProductType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shoh Jahon on 3/31/2019.
 */
public class ProductTypeDaoImpl implements ProductTypeDao {
    private final Connection connection;
    private final String INSERT_SQL = "INSERT INTO product_types (product_type) VALUES (?)";
    private final String UPDATE_SQL = "UPDATE product_types SET  product_type = ? WHERE id = ?";
    private final String DELETE_SQL = "DELETE FROM product_types WHERE id = ?";
    private final String SELECT_ONE_SQL = "SELECT * FROM product_types WHERE id = ?";
    private final String SELECT_ALL_SQL = "SELECT * FROM product_types";
    private final String SELECT_BY_NAME_SQL = "SELECT * FROM product_types WHERE product_type LIKE ? LIMIT 1";

    public ProductTypeDaoImpl(Connection connection) {
        this.connection = connection;
    }


    @Override
    public ProductType findProductTypeByType(String type) throws Exception {
        ProductType productType = null;
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_NAME_SQL)){
            connection.setAutoCommit(false);
            statement.setString(1,type);
            try (ResultSet result = statement.executeQuery()){
                if (result.next()){
                    productType = new ProductType(result.getString("product_type"));
                    productType.setId(result.getInt("id"));
                }
            }
            connection.commit();
        }catch (SQLException ex){
            connection.rollback();
            throw new Exception(ex.getMessage());
        }finally {
            connection.setAutoCommit(true);
            return productType;
        }
    }

    @Override
    public ProductType findProductTypeById(Integer id) throws Exception {
        ProductType productType = null;
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ONE_SQL)){
            connection.setAutoCommit(false);
            statement.setInt(1,id);
            try (ResultSet result = statement.executeQuery()){
                if (result.next()){
                    productType = new ProductType(result.getString("product_type"));
                    productType.setId(result.getInt("id"));
                }
            }
            connection.commit();
        }catch (SQLException ex){
            connection.rollback();
            throw new Exception(ex.getMessage());
        }finally {
            connection.setAutoCommit(true);
            return productType;
        }
    }

    @Override
    public void deleteProductTypeById(Integer id) throws Exception {
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
    public void insetProductType(ProductType productType) throws Exception {
        try(PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            connection.setAutoCommit(false);
            statement.setString(1,productType.getProductType());
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
    public List<ProductType> findAllProductTypes() throws Exception {
        List<ProductType> list = new ArrayList<>();
        try (Statement statement = connection.createStatement()){
            try (ResultSet result = statement.executeQuery(SELECT_ALL_SQL)){
                while (result.next()){
                    ProductType type = new ProductType(result.getString("product_type"));
                    type.setId(result.getInt("id"));
                    list.add(type);
                }
            }
        }catch (SQLException ex){
            throw new Exception(ex.getMessage());
        }finally {
            return list;
        }
    }

    @Override
    public void updateProductType(ProductType productType) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)){
            connection.setAutoCommit(false);
            statement.setString(1,productType.getProductType());
            statement.setInt(2,productType.getId());
            statement.executeUpdate();
            connection.commit();
        }catch (SQLException ex){
            connection.rollback();
            throw new Exception(ex.getMessage());
        }finally {
            connection.setAutoCommit(true);
        }
    }

}

