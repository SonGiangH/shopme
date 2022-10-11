package com.shopme.admin.brand;

import com.shopme.admin.category.CategoryDTO;
import com.shopme.admin.exception.BrandNotFoundException;
import com.shopme.admin.exception.BrandNotFoundRestException;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class BrandRestController {

    @Autowired
    public BrandService brandService;

    @PostMapping("/brands/check_unique")
    public String checkDuplicateName(@Param("id") Integer id,
                                     @Param("name") String name) {
        return brandService.checkUniqueBrand(id, name);
    }

    // Get Categories By Brand Id
    @GetMapping("/brands/{id}/categories")
    public List<CategoryDTO> listCategoriesByBrandId(@PathVariable(name = "id") Integer brandId) throws BrandNotFoundRestException {
        // Create new dto list
        List<CategoryDTO> categoryDTOList = new ArrayList<>();

        try {
            // get list of category from DB by brand ID
            Brand brand = brandService.getBrandById(brandId);
            Set<Category> categories = brand.getCategories();
            for (Category cat : categories) {
                CategoryDTO dto = new CategoryDTO(cat.getId(), cat.getName());
                categoryDTOList.add(dto);
            }
            return categoryDTOList;
        } catch (BrandNotFoundException e) {
            throw new BrandNotFoundRestException();
        }
    }
}
