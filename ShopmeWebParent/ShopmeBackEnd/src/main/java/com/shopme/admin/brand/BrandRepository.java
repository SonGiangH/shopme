package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {

    // Count brand by ID
    Long countById(Integer id);

    // Find brand by Name
    Brand findByName(String name);

    // Find all brand
    @Query("select new Brand(b.id, b.name) from Brand b order by b.name asc")
    public List<Brand> findAll();
}
