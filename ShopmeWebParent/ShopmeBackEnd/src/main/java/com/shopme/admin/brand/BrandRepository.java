package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {

    // Count brand by ID
    Long countById(Integer id);

    // Find brand by Name
    Brand findByName(String name);
}
