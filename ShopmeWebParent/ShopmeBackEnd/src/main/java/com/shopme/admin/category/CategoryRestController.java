package com.shopme.admin.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryRestController {

    @Autowired
    public CategoryService categoryService;

    @PostMapping("/categories/check_unique")
    public String checkDuplicateNameAlias(@Param("id") Integer id,
                                          @Param("name") String name,
                                          @Param("alias") String alias) {
        return categoryService.checkUniqueCategory(id, name,alias);
    }
}
