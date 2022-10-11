package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Arrays;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CategoryRepositoryTests {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void createMainCategoryTest() {
        Category electronics = new Category("Electronics", "electronics");
        Category savedCategory = categoryRepository.save(electronics);

        assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void createSubCategoryTest() {
        Category parent = categoryRepository.findById(8).get();

        Category memory = new Category("Memory","memory", parent);
        Category ram    = new Category("Ram", "ram", parent);


       categoryRepository.saveAll(Arrays.asList(memory, ram));
    }

    @Test
    public void getCategoryTest() {
        Category category = categoryRepository.findById(2).get();
        System.out.println(category.getName());

        // print out children category
        Set<Category> children = category.getChildren();
//        for (Category subCategory : children) {
//            System.out.println(subCategory.getName());
//        }
        children.forEach(subCategory -> System.out.println(subCategory.getName()));
    }

    @Test
    public void printHierarchyCategoryTest() {
        Iterable<Category> categories = categoryRepository.findAll();
        for (Category category : categories) {
            if (category.getParent() == null) {
                System.out.println(category.getName());

                Set<Category> children = category.getChildren();
                for (Category subCategory : children) {
                    System.out.println("--" + subCategory.getName());
                    printChildren(subCategory, 1);
                }
            }
        }
    }

    private void printChildren (Category parent, int level) {
        Set<Category> children = parent.getChildren();
        for (Category category : children) {
            for (int i =0; i < level +1; i++) {
                System.out.print("--");
            }
            System.out.println(category.getName());

            //recursive 1 more layer
            printChildren(category, level + 1);
        }
    }

    // Test find by name of category
    @Test
    public void findByNameTest() {
        String name = "Computers";
        Category category = categoryRepository.findByName(name);

        assertThat(category).isNotNull();
        assertThat(category.getName()).isEqualTo(name);
    }

    // Test find by alias of category
    @Test
    public void findByAliasTest() {
        String alias = "pc";
        Category category = categoryRepository.findByAlias(alias);

        assertThat(category).isNotNull();
        assertThat(category.getAlias()).isEqualTo(alias);
    }
}
