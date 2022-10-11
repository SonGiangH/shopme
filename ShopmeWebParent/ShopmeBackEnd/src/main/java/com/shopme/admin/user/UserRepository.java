package com.shopme.admin.user;

import com.shopme.common.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends PagingAndSortingRepository<User,Integer> {

    // Get User by Email from database
    @Query("select u from User u where u.email = :email ")
    User getUserByEmail(@Param("email") String email);

    // Count User By ID
    Long countById(Integer id);

    // Update enable / disable status of user by ID
    @Modifying
    @Query("update User u set u.enabled = ?2 where u.id = ?1")
     void updateEnabledStatus(Integer id, boolean enabled);
}
