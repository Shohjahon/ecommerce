package sample.dao;

import sample.model.ProductType;

import java.util.List;

/**
 *  Shoh Jahon tomonidan 3/31/2019 da qo'shilgan.
 */
public interface ProductTypeDao {

    ProductType findProductTypeByType(String type) throws Exception;

    ProductType findProductTypeById(Integer id) throws Exception;

    void deleteProductTypeById(Integer id) throws Exception;

    void insetProductType(ProductType productType) throws Exception;

    List<ProductType> findAllProductTypes() throws Exception;

    void updateProductType(ProductType productType) throws Exception;

}
