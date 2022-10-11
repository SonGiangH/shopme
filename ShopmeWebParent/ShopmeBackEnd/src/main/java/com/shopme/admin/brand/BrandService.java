package com.shopme.admin.brand;

import com.shopme.admin.exception.BrandNotFoundException;
import com.shopme.common.entity.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class BrandService {

    @Autowired
    private BrandRepository repo;

    // Get all Brands
    public List<Brand> getAllBrands() {
        return (List<Brand>) repo.findAll();
    }

    // Save Brand
    public Brand save(Brand brand) {
        return repo.save(brand);
    }

    // Get brand by ID
    public Brand getBrandById(Integer id) throws BrandNotFoundException {
        try {
            return repo.findById(id).get();
        } catch ( NoSuchElementException exception) {
            throw new BrandNotFoundException("No Brand was found with ID: " + id);
        }
    }

    // Delete brand by Id
    public void deleteBrandById(Integer id) throws BrandNotFoundException {
        Long count = repo.countById(id);

        if (count == null || count == 0) {
            throw new BrandNotFoundException("No Brand was found with ID: " + id);
        }

        repo.deleteById(id);
    }

    // Check unique name of brand
    public String checkUniqueBrand(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);

        Brand brandByName = repo.findByName(name);

        if (isCreatingNew) {
            if (brandByName != null) return "DuplicateName";
        } else {
            // Edit Mode
            if (brandByName != null && brandByName.getId() != id)
                return "DuplicateName";
        }
        return "OK";
    }
}
