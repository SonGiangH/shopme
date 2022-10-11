package com.shopme.admin.category;

import com.shopme.admin.exception.CategoryNotFoundException;
import com.shopme.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@Transactional
public class CategoryService {
    public static final int CATEGORIES_PER_PAGE = 6;

    @Autowired
    private CategoryRepository categoryRepository;

    // Get all category
    public List<Category> getAllCategory() {
        return (List<Category>) categoryRepository.findAll();
    }

    // Get hierarchy category from DB
    public List<Category> listAllCategoriesInForm () {
        // new array list
        List<Category> listCategoriesInForm = new ArrayList<>();

        // get all category from database
        Iterable<Category> categoriesInDb = categoryRepository.findAll();

        for (Category category : categoriesInDb) {
            if (category.getParent() == null) {
                listCategoriesInForm.add(Category.copyIdAndNameCategory(category));

                Set<Category> children = category.getChildren();
                for (Category subCategory : children) {
                    String name = "--" + subCategory.getName();

                    listCategoriesInForm.add(Category.copyIdAndNameCategory(subCategory.getId(), name));

                    listChildren(listCategoriesInForm, subCategory, 1);
                }
            }
        }
        return listCategoriesInForm;
    }

    public static void listChildren(List<Category> listCategoriesInForm ,Category parent, int level) {
        Set<Category> children = parent.getChildren();
        for (Category category : children) {
            String name = "";

            for (int i =0; i < level +1; i++) {
                name += "--";
            }
            name += category.getName();
            listCategoriesInForm.add(Category.copyIdAndNameCategory(category.getId(), name));

            //recursive 1 more layer
            listChildren(listCategoriesInForm, category, level + 1);
        }
    }

    // Save Category to Database
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    // Get Category from ID
    public Category findCategoryById(Integer id) throws CategoryNotFoundException {
        try {Category category = categoryRepository.findById(id).get();
            return category;
        } catch (NoSuchElementException ex) {
            throw new CategoryNotFoundException("No Category was found with ID :" + id);
        }
    }

    // Check uniqueness of Category
    public String checkUniqueCategory(Integer id, String name, String alias) {
        boolean isCreatingNew = (id == null);

        Category categoryByName = categoryRepository.findByName(name);

        if (isCreatingNew) {
            if (categoryByName != null) return "DuplicateName";
                else {
                    Category categoryByAlias = categoryRepository.findByAlias(alias);
                    if (categoryByAlias != null)  return "DuplicateAlias";
                }
            }
            // Edit Mode
            else {
                // Check existing category has different id with given ID
                if (categoryByName != null && categoryByName.getId() != id)
                    return "DuplicateName";
                else {
                    Category categoryByAlias = categoryRepository.findByAlias(alias);
                    if (categoryByAlias != null && categoryByAlias.getId() != id)
                        return "DuplicateAlias";
                }
            }
        return "OK";
    }

    // Delete category by ID
    public void deleteCategoryById(Integer id) throws CategoryNotFoundException {
        Long count = categoryRepository.countById(id);

        if (count == null || count ==0) {
            throw new CategoryNotFoundException("No category was found with ID : "+ id);
        }
        categoryRepository.deleteById(id);
    }

    // Update enabled status of category
    public void updateEnableStatus(Integer id, boolean enabled) {
        categoryRepository.updateEnabledStatus(id, enabled);
    }

    // Pagination Categories
    public Page<Category> getCategoryByPage(int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, CATEGORIES_PER_PAGE);
        return categoryRepository.findAll(pageable);
    }
}
