package com.shopme.admin.product;

import com.shopme.common.entity.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {

    Product findByName(String name);

    // Update enable status
    @Modifying
    @Query("update Product  p set p.enabled= ?2 where p.id = ?1")
    void updateEnabledStatus(Integer id, boolean enabled);

    // Count by ID for deleting
    Long countById(Integer id);
}
