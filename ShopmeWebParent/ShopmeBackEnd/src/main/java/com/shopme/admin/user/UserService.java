package com.shopme.admin.user;

import com.shopme.admin.exception.NoUserNotFoundException;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserService {

    public static final int USERS_PER_PAGE = 4;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public List<User> listAllUser() {

        return (List<User>) userRepository.findAll();
    }

    public List<Role> listAllRole() {
        return (List<Role>) roleRepository.findAll();
    }

    // Save User to database
    public User save(User user) {
        // update user or create new user, updating -> use ID not null
        boolean isUpdatingUser = (user.getId() != null);

        // Edit Mode
        if (isUpdatingUser) {
            // get User from database
            User existingUser = userRepository.findById(user.getId()).get();

            // password input = empty
            if (user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            }
        }
        return userRepository.save(user);
    }

    // isUserEmailUnique
    public boolean isEmailUnique(String email, Integer id) {
        User userByEmail = userRepository.getUserByEmail(email);
        System.out.println("email :" + email);
        System.out.println("id :" + id);
        // If not found -> null -> true -> OK
        if (userByEmail == null) return true;

        // Create New User -> user by edit ID == null -> duplicated , Else -> Edit mode ->
        boolean isCreatingNew = (id == null);
        if (isCreatingNew) {
            if (userByEmail != null) {
                return false;  // duplicated
            }
        } else {
            if (userByEmail.getId() != id) { // if ID cua userByEmail != id -> exist -> duplicated
                return false;
            }
        }
        return true;
    }

    // Get User by ID
    public User getUserById(Integer id) throws NoUserNotFoundException {
        try{
            return userRepository.findById(id).get();
        } catch (NoSuchElementException exception) {
            throw new NoUserNotFoundException("No user was found with ID: "+ id);
        }
    }
    // Delete User by ID
    public void deleteUserById(Integer id) throws NoUserNotFoundException {
        Long count = userRepository.countById(id);

        if (count == null || count == 0) {
            throw new NoUserNotFoundException("No user was found with ID: "+ id);
        }
        userRepository.deleteById(id);
    }
    // Update Status Enable of User
    public void updateEnabledUser(Integer id, boolean enabled) {
        userRepository.updateEnabledStatus(id, enabled);
    }

    // Pagination Users
    public Page<User> getUsersByPage(int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, USERS_PER_PAGE);
        return userRepository.findAll(pageable);
    }
}
