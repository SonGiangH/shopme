package com.shopme.admin.product;

import com.shopme.admin.exception.ProductNotFoundException;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    // Find all products
    public List<Product> findAllProducts() {
        return (List<Product>) productRepository.findAll();
    }

    // Save product
    public Product save(Product product) {
        if (product.getId() == null) {
            product.setCreatedTime(new Date());
        };

        if (product.getAlias() == null || product.getAlias().isEmpty()) {
            String defaultAlias = product.getName().replace(" ", "-");
            product.setAlias(defaultAlias);
        } else {
            product.getAlias().replace(" ", "-");
        }

        product.setUpdatedTime(new Date());

        return productRepository.save(product);
    }

    // Check unique name of product
    public String checkUniqueProduct(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);

        Product productByName = productRepository.findByName(name);

        if (isCreatingNew) {
            if (productByName != null) return "DuplicateName";
        } else {
            // Edit Mode
            if (productByName != null && productByName.getId() != id)
                return "DuplicateName";
        }
        return "OK";
    }

    // Update enable status
    public void updateEnableStatus(Integer id, boolean enabled) {
        productRepository.updateEnabledStatus(id, enabled);
    }

    // Delete product by Id
    public void deleteProductById(Integer id) throws ProductNotFoundException {
        Long count = productRepository.countById(id);

        if (count == null || count ==0) {
            throw new ProductNotFoundException("No Product was found with ID: " + id);
        }
        productRepository.deleteById(id);
    }

    // get Product By Id
    public Product getProductById(Integer id) throws ProductNotFoundException {
        try {
            return productRepository.findById(id).get();
        } catch (NoSuchElementException err) {
            throw new ProductNotFoundException("No Product was found with ID: " + id);
        }
    }
}
