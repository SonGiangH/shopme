package com.shopme.admin.user;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.exception.NoUserNotFoundException;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

//    @GetMapping("/users")
//    public String listUser(Model model) {
//        List<User> listUser = userService.listAllUser();
//        model.addAttribute("listUsers", listUser);
//        return "users";
//    }

    @GetMapping("/users")
    public String listFirstPageUser(Model model) {
        listUsersByPage(1,model);
        return "user/users";
    }

    // Create New User
    @GetMapping("/users/new")
    public String createUser(Model model) {
        User user = new User();
        List<Role> listRoles = userService.listAllRole();

        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("pageTitle", "Creat New User");
        return "user/user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam(name = "avatar")MultipartFile multipartFile) throws IOException {

        if (!multipartFile.isEmpty()) {

            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhoto(fileName);
            User savedUser =  userService.save(user);

            String uploadDirectory = "user-photos/" + savedUser.getId();
            // Delete all pics before save new pics
            FileUploadUtil.cleanDir(uploadDirectory);
            FileUploadUtil.saveFile(uploadDirectory,fileName,multipartFile);
        } else {
            if (user.getPhoto().isEmpty()) {
                user.setPhoto(null);
                userService.save(user);
            }
        }

        redirectAttributes.addFlashAttribute("message", "User has been saved successfully");
        return "redirect:/users";
    }

    // Edit Existing User
    @GetMapping("users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getUserById(id);
            List<Role> listRoles = userService.listAllRole();
            model.addAttribute("user", user);
            model.addAttribute("listRoles", listRoles);
            model.addAttribute("pageTitle", "Update Existing User (ID: " + user.getId()+")");

            return "/user/user_form";
        } catch (NoUserNotFoundException err) {
            redirectAttributes.addFlashAttribute("message", err.getMessage());
            return "redirect:/users";
        }
    }

    // Delete User
    @GetMapping("users/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id,
                             RedirectAttributes redirectAttributes,Model model) {
        try {
            userService.deleteUserById(id);
            redirectAttributes.addFlashAttribute("message", "Delete User (ID: " + id+") successfully !");

        } catch (NoUserNotFoundException err) {
            redirectAttributes.addFlashAttribute("message", err.getMessage());
        }
        return "redirect:/users";
    }

    // Update Enabled Status
    @GetMapping("users/{id}/enable/{status}")
    public String updateStatusUser(@PathVariable("id") Integer id,
                                   @PathVariable("status") boolean enabled,
                                   RedirectAttributes redirectAttributes) {
        userService.updateEnabledUser(id, enabled);
        String action = enabled ? "Enable" : "Disable";
        redirectAttributes.addFlashAttribute("message", action + " User(ID: " + id + ") successfully");
        return "redirect:/users";
    }

    // List Users By Page
    @GetMapping("/users/page/{pageNum}")
    public String listUsersByPage(@PathVariable("pageNum") Integer pageNum, Model model) {
        Page<User> pageUser =  userService.getUsersByPage(pageNum-1);
        List<User> listUsers = pageUser.getContent();

        long totalItems = pageUser.getTotalElements();
        long startCount = (pageNum - 1) * userService.USERS_PER_PAGE +1;
        long endCount = startCount + userService.USERS_PER_PAGE -1;
        if (endCount > totalItems) {
            endCount = totalItems;
        }
        long totalPage = pageUser.getTotalPages();
        // Pass to View page by model
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("listUsers", listUsers);
        return "/user/users";
    }
}
