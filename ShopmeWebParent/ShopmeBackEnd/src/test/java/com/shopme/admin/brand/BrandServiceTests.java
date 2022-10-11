package com.shopme.admin.brand;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.query.JSqlParserUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class BrandServiceTests {

    @MockBean
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @Test
    public void checkInputUniqueInNewModeWithDuplicateName() {
        Integer id = null;
        String name = "Samsung";

        Brand brand = new Brand(id, name);

        Mockito.when(brandRepository.findByName(name)).thenReturn(brand);

        String result = brandService.checkUniqueBrand(id, name);

        Brand savedBrand = brandRepository.findByName(name);

        assertThat(result).isEqualTo("DuplicateName");
    }

    @Test
    public void checkInputUniqueInNewModeWithOK() {
        Integer id = null;
        String name = "NameAbc";

        Mockito.when(brandRepository.findByName(name)).thenReturn(null);

        String result = brandService.checkUniqueBrand(id, name);

        assertThat(result).isEqualTo("OK");
    }

    // Edit Mode (id = valid)
    @Test
    public void checkIsUniqueInEditModeWithDuplicateName() {
        Integer id = 5;
        String name = "Dell";

        Brand brand = new Brand(2, name);

        Mockito.when(brandRepository.findByName(name)).thenReturn(brand);

        String result = brandService.checkUniqueBrand(id, name);

        assertThat(result).isEqualTo("DuplicateName");
    }

    @Test
    public void checkIsUniqueInEditModeReturnOK() {
        Integer id = 1;
        String name = "NameABC";

        Mockito.when(brandRepository.findByName(name)).thenReturn(null);

        String result = brandService.checkUniqueBrand(id, name);

        assertThat(result).isEqualTo("OK");
    }
}
