package com.shopme.admin.category;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.exception.CategoryNotFoundException;
import com.shopme.common.entity.Category;
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

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Get All Categories
//    @GetMapping("/categories")
//    public String getAllCategories(Model model)  {
//        List<Category> listCategories = categoryService.getAllCategory();
//        model.addAttribute("listCategories", listCategories);
//        return "/category/categories";
//    }

    // List the first page of Category
    @GetMapping("/categories")
    public String listFirstPageCategory(Model model) {
        listCategoryByPage(1, model);
        return "category/categories";
    }

    // Create new category
    @GetMapping("/categories/new")
    public String createNewCategory(Model model) {
        List<Category> listCategories = categoryService.listAllCategoriesInForm();

        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create New Category");
        model.addAttribute("category", new Category());

        return "/category/category_form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category, RedirectAttributes redirectAttributes,
                           @RequestParam(name = "categoryImage") MultipartFile multipartFile) throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        category.setImage(fileName);
        Category savedCategory =  categoryService.save(category);

        String uploadDirectory = "../category-photos/" + savedCategory.getId();

        // Delete all pics before save new pics
        FileUploadUtil.cleanDir(uploadDirectory);
        FileUploadUtil.saveFile(uploadDirectory,fileName,multipartFile);

        redirectAttributes.addFlashAttribute("message", "Category has been saved successfully");
        return "redirect:/categories";
    }

    // Update Existing Category
    @GetMapping("/categories/edit/{id}")
    public String updateCategory(@PathVariable(name = "id") Integer id, Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.findCategoryById(id);

            List<Category> listCategories = categoryService.listAllCategoriesInForm();

            model.addAttribute("listCategories", listCategories);
            model.addAttribute("category", category);
            model.addAttribute("pageTitle", "Update Existing Category (ID: " + category.getId()+")");

            return "/category/category_form";

        } catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/categories";
        }
    }

    // Delete Category
    @GetMapping("/categories/delete/{id}")
    public String deleteCategoryById(@PathVariable(name = "id") Integer id,
                                     RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategoryById(id);
            redirectAttributes.addFlashAttribute("message", "Delete Category (ID: " + id+") successfully !");

        } catch (CategoryNotFoundException e) {

            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/categories";
    }
    // Update Enable Status
    @GetMapping("/categories/{id}/enable/{status}")
    public String updateEnableStatusCategory(@PathVariable(name = "id") Integer id,
                                           @PathVariable(name = "status") boolean enabled,
                                             RedirectAttributes redirectAttributes) {
        categoryService.updateEnableStatus(id, enabled);

        String action = enabled ? "Enable" : "Disable";
        redirectAttributes.addFlashAttribute("message", action + " Category (ID: " + id + ") successfully");
        return "redirect:/categories";
    }

    // List Category By Page
    @GetMapping("/categories/page/{pageNum}")
    public String listCategoryByPage(@PathVariable(name = "pageNum") Integer pageNum, Model model) {
        Page<Category> pageCategory = categoryService.getCategoryByPage(pageNum-1);
        List<Category> listCategories = pageCategory.getContent();

        long totalItems = pageCategory.getTotalElements();
        long startCount = (pageNum-1)*categoryService.CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + categoryService.CATEGORIES_PER_PAGE -1;
        if (endCount > totalItems) {
            endCount = totalItems;
        }
        long totalPage = pageCategory.getTotalPages();
        // Pass to View page by model
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("listCategories", listCategories);

        return "/category/categories";
    }
}
