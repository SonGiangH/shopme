package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import static org.assertj.core.api.Assertions.*;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class BrandRepositoryTest {

    @Autowired
    public BrandRepository brandRepository;

    @Autowired
    public EntityManager entityManager;

    @Test
    public void createBrandOneCategoryTest() {
        Brand newBrand = new Brand("Samsung", "default-logo.png");

        // get Category in db
        Category smartphone = entityManager.find(Category.class, 4);

        newBrand.addCategories(smartphone);

        Brand savedCategory = brandRepository.save(newBrand);
        assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void createBrandTwoCategoryTest() {
        Brand newBrand = new Brand("Apple", "default-logo.png");

        // get Category in db
        Category cellphones = entityManager.find(Category.class, 4);
        Category tablets = entityManager.find(Category.class, 7);

        newBrand.addCategories(cellphones);
        newBrand.addCategories(tablets);

        Brand savedCategory = brandRepository.save(newBrand);
        assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void createBrandTwoSubCategoryTest() {
        Brand newBrand = new Brand("Acer", "default-logo.png");

        // get Category in db
        Category memory = entityManager.find(Category.class, 29);
        Category internalHardDrive = entityManager.find(Category.class, 24);

        newBrand.addCategories(memory);
        newBrand.addCategories(internalHardDrive);

        Brand savedCategory = brandRepository.save(newBrand);
        assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    // List all brands
    @Test
    public void listAllBrandsTest() {
        Iterable<Brand> brandsList = brandRepository.findAll();
        brandsList.forEach(brand -> System.out.println(brand));
    }

    // Get brand by ID
    @Test
    public void getBrandById() {
        Brand brand = brandRepository.findById(2).get();
        System.out.println(brand);

        assertThat(brand.getName()).isEqualTo("Samsung Electronics");
    }

    // Update Brand
    @Test
    public void updateBrandByIdTest() {
        Brand brand = brandRepository.findById(2).get();

        brand.setName("Samsung Electronics");
        brandRepository.save(brand);
    }

    // Delete Brand by ID
    @Test
    public void deleteBrandByIdTest() {
        brandRepository.deleteById(2);
        Optional<Brand> result = brandRepository.findById(2);

        assertThat(result).isEmpty();
    }

}
