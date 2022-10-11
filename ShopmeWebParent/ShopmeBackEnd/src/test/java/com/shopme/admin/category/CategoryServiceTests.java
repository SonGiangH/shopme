package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {

    @MockBean
    private CategoryRepository repository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    public void checkIsUniqueInNewModeWithDuplicateName() {
        Integer id = null;
        String name = "Computers";
        String alias = "abc";

        Category category = new Category(id, name, alias);

        Mockito.when(repository.findByName(name)).thenReturn(category);
        Mockito.when(repository.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUniqueCategory(id, name, alias);

        assertThat(result).isEqualTo("DuplicateName");
    }

    @Test
    public void checkIsUniqueInNewModeWithDuplicateAlias() {
        Integer id = null;
        String name = "NameABC";
        String alias = "pc";

        Category category = new Category(id, name, alias);

        Mockito.when(repository.findByName(name)).thenReturn(null);
        Mockito.when(repository.findByAlias(alias)).thenReturn(category);

        String result = categoryService.checkUniqueCategory(id, name, alias);

        assertThat(result).isEqualTo("DuplicateAlias");
    }

    @Test
    public void checkIsUniqueInNewModeReturnOK() {
        Integer id = null;
        String name = "NameABC";
        String alias = "abc";

        Mockito.when(repository.findByName(name)).thenReturn(null);
        Mockito.when(repository.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUniqueCategory(id, name, alias);

        assertThat(result).isEqualTo("OK");
    }

    // Edit Mode (id = valid)
    @Test
    public void checkIsUniqueInEditModeWithDuplicateName() {
        Integer id = 5;
        String name = "Smartphone";
        String alias = "abc";

        Category category = new Category(2, name, alias);

        Mockito.when(repository.findByName(name)).thenReturn(category);
        Mockito.when(repository.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUniqueCategory(id, name, alias);

        assertThat(result).isEqualTo("DuplicateName");
    }

    @Test
    public void checkIsUniqueInEditModeWithDuplicateAlias() {
        Integer id = 1;
        String name = "NameABC";
        String alias = "pc";

        Category category = new Category(2, name, alias);

        Mockito.when(repository.findByName(name)).thenReturn(null);
        Mockito.when(repository.findByAlias(alias)).thenReturn(category);

        String result = categoryService.checkUniqueCategory(id, name, alias);

        assertThat(result).isEqualTo("DuplicateAlias");
    }

    @Test
    public void checkIsUniqueInEditModeReturnOK() {
        Integer id = 1;
        String name = "NameABC";
        String alias = "abc";

        Mockito.when(repository.findByName(name)).thenReturn(null);
        Mockito.when(repository.findByAlias(alias)).thenReturn(null);

        String result = categoryService.checkUniqueCategory(id, name, alias);

        assertThat(result).isEqualTo("OK");
    }
}
