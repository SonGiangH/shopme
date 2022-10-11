package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {

    Category findByName(String name);

    Category findByAlias(String alias);

    // Count Category By ID
    Long countById(Integer id);

    // Update enable /disable status of category by ID
    @Modifying
    @Query("update Category c set c.enabled= ?2 where c.id = ?1")
    void updateEnabledStatus(Integer id, boolean enabled);
}
