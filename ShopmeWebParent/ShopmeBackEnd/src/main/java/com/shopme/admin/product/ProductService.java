package com.shopme.admin.product;

import com.shopme.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

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
        return productRepository.save(product);
    }
}
