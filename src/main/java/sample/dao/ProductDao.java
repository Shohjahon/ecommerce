package sample.dao;

import sample.model.Product;

import java.util.List;

/**
 *  Shoh Jahon tomonidan 3/31/2019 da qo'shilgan.
 */
public interface ProductDao {

    Product findProductById(Integer id) throws Exception;

    void deleteProduct(Integer id) throws Exception;

    void insertProduct(Product product) throws Exception;

    void updateProduct(Product product) throws Exception;

    List<Product> findAllProducts() throws Exception;
}
