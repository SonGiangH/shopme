package com.shopme.admin.user;



import com.shopme.common.entity.Role;
import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Arrays;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository repository;

    @Test
    public void createFirstRoleTest() {
        Role roleAdmin = new Role("Admin", "Manage everything");
        Role savedRoleAdmin = repository.save(roleAdmin);
        assertThat(savedRoleAdmin.getId()).isGreaterThan(0);
    }

    @Test
    public void createRestRoles() {
        Role roleSalePerson = new Role ("Saleperson", "Manage product price, customers, shipping orders and reports");
        Role roleEditor = new Role("Editor", "Manage categories, brands and products, articles and menus");
        Role roleShipper = new Role("Shipper", "View Product, view order and update order status");
        Role roleAssistant = new Role("Assistant", "Manage questions and reviews");

        repository.saveAll(Arrays.asList(roleSalePerson, roleEditor, roleShipper, roleAssistant));
    }
}
