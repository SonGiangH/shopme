package com.shopme.admin.user;

import com.shopme.admin.user.UserRepository;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import java.util.List;


@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class UserRepositoryTest {

    @Autowired
    public UserRepository repository;

    @Autowired
    public EntityManager entityManager;

    @Test
    public void createOneRoleUserTest() {
        User userAdmin = new User("hongson@gmail.com", "123456", "Hong", "Son");

        // get Role Admin from database with entity manager provided by Spring
        Role roleAdmin = entityManager.find(Role.class, 1);

        userAdmin.addRole(roleAdmin);

        User savedUser = repository.save(userAdmin);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void createTwoRoleUserTest() {
        User userSecond = new User("obama@gmail.com", "123456aA", "Obama", "Barrack");

        // get Role from Role Model
        Role roleEditor = new Role(3);
        Role roleAssistant = new Role(5);

        userSecond.addRole(roleAssistant);
        userSecond.addRole(roleEditor);

        User savedUser = repository.save(userSecond);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void listAllUserTest() {
        Iterable<User> userList = repository.findAll();
        userList.forEach(user -> System.out.println(user));
    }

    @Test
    public void getUserByIDTest() {
        User user = repository.findById(2).get();
        System.out.println(user);

        assertThat(user).isNotNull();
    }

    @Test
    public void updateUserByIdTest() {
        User user = repository.findById(2).get();

        Role roleShipper = new Role(4);
        Role roleAssistant = new Role(5);

        user.setEnabled(true);
        user.setEmail("Obama.WhiteHouse@yahoo.com");

        user.getRoles().remove(roleAssistant);
        user.addRole(roleShipper);

        repository.save(user);
    }

    @Test
    public void deleteUserByIdTest() {
        Integer userId = 2;
        repository.deleteById(userId);
    }

    // Test get User by Email
    @Test
    public void getUserByEmailTest() {
        String email = "obama@gmail.com";
        User user = repository.getUserByEmail(email);

        assertThat(user).isNotNull();
    }

    // Test update enable user
    @Test
    public void testDisableUser() {
        Integer userId = 2;
        repository.updateEnabledStatus(userId, false);
    }
    // Test paging
    @Test
    public void testPagingContent() {
        int pageSize = 4;
        int pageNumber = 1; // page start from 0

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> page = repository.findAll(pageable);

        List<User> listUsers = page.getContent();
        listUsers.forEach(user -> System.out.println(user));
        assertThat(listUsers.size()).isEqualTo(pageSize);
    }
}
