package sample.dao.impl;

import sample.dao.SalesmanDao;
import sample.model.Salesman;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shoh Jahon on 3/31/2019.
 */
public class SalesmanDaoImpl implements SalesmanDao {
    private final Connection connection;
    private final String INSERT_SQL = "INSERT INTO salesman (full_name,phone_number,address) VALUES (?,?,?)";
    private final String UPDATE_SQL = "UPDATE salesman SET  full_name = ?, phone_number = ?, address = ? WHERE id = ?";
    private final String DELETE_SQL = "DELETE FROM salesman WHERE id = ?";
    private final String SELECT_ONE_SQL = "SELECT * FROM salesman WHERE id = ?";
    private final String SELECT_ALL_SQL = "SELECT * FROM salesman";

    public SalesmanDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Salesman findSalesmanById(Integer id) throws Exception {
        Salesman salesman = null;
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ONE_SQL)){
            connection.setAutoCommit(false);
            statement.setInt(1,id);
            try (ResultSet result = statement.executeQuery()){
                if (result.next()){
                    salesman = new Salesman();
                    salesman.setId(result.getInt("id"));
                    salesman.setFullName(result.getString("full_name"));
                    salesman.setPhoneNumber(result.getString("phone_number"));
                    salesman.setAddress(result.getString("address"));
                }
            }
            connection.commit();
        }catch (SQLException ex){
            connection.rollback();
            throw new Exception(ex.getMessage());
        }finally {
            connection.setAutoCommit(true);
            return salesman;
        }
    }

    @Override
    public void insertSalesman(Salesman salesman) throws Exception {
        try(PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            connection.setAutoCommit(false);
            statement.setString(1,salesman.getFullName());
            statement.setString(2,salesman.getPhoneNumber());
            statement.setString(3,salesman.getAddress());
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
    public void deleteSalesman(Integer id) throws Exception {
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
    public void updateSalesman(Salesman salesman) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)){
            connection.setAutoCommit(false);
            statement.setString(1,salesman.getFullName());
            statement.setString(2,salesman.getPhoneNumber());
            statement.setString(3,salesman.getAddress());
            statement.setInt(4,salesman.getId());
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
    public List<Salesman> findAllSalesmans() throws Exception {
        List<Salesman> list = new ArrayList<>();
        try (Statement statement = connection.createStatement()){
            try (ResultSet result = statement.executeQuery(SELECT_ALL_SQL)){
                while (result.next()){
                    Salesman salesman = new Salesman();
                    salesman.setId(result.getInt("id"));
                    salesman.setFullName(result.getString("full_name"));
                    salesman.setPhoneNumber(result.getString("phone_number"));
                    salesman.setAddress(result.getString("address"));
                    list.add(salesman);
                }
            }
        }catch (SQLException ex){
            throw new Exception(ex.getMessage());
        }finally {
            return list;
        }
    }
}
