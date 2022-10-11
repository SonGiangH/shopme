package com.shopme.admin.product;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class ProductRepositoryTest {

    @Autowired
    public ProductRepository repository;

    @Autowired
    public TestEntityManager entityManager;

    @Test
    public void createFirstProductTest() {
        Brand brand = entityManager.find(Brand.class, 10);
        Category category = entityManager.find(Category.class, 4);

        Product product = new Product();
        product.setName("Samsung Galaxy A31");
        product.setAlias("ss galaxy a31");
        product.setShortDescription("A very good smartphone");
        product.setFullDescription("This is full description of A31 smartphone");
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        product.setPrice(456);
        product.setBrand(brand);
        product.setCategory(category);

        Product savedProduct = repository.save(product);

        // Assume create Object successfully (id > 0)
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);
    }

    @Test
    public void createSecondProductTest() {
        Brand brand = entityManager.find(Brand.class, 38);
        Category category = entityManager.find(Category.class, 6);

        Product product = new Product();
        product.setName("Dell Inspiron 2020");
        product.setAlias("dell inspiron 2020");
        product.setShortDescription("A very good laptop");
        product.setFullDescription("This is full description of dell inspiron laptop");
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());

        product.setPrice(456);
        product.setBrand(brand);
        product.setCategory(category);
        product.setInStock(true);
        product.setEnabled(true);

        Product savedProduct = repository.save(product);

        // Assume create Object successfully (id > 0)
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isGreaterThan(0);
    }

    @Test
    public void listAllProductsTest() {
        Iterable<Product> productList = repository.findAll();

        productList.forEach(product -> System.out.println(product));
    }

    @Test
    public void getProductById() {
        Integer id = 2;
        Product product = repository.findById(id).get();

        assertThat(product).isNotNull();
    }

    @Test
    public void updateProductById() {
        Integer id = 1;
        Product product = repository.findById(id).get();

        product.setPrice(999);

        Product updatedProduct = repository.findById(id).get();
        assertThat(updatedProduct.getPrice()).isEqualTo(999);
    }

    @Test
    public void deleteProductById() {
        Integer id = 2;
        repository.deleteById(id);
        Optional<Product> product = repository.findById(id);

        assertThat(!product.isPresent());

    }
}
