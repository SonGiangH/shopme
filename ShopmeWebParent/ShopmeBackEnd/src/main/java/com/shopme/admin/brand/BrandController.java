package com.shopme.admin.brand;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.exception.BrandNotFoundException;
import com.shopme.common.entity.Brand;
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
public class BrandController {

    @Autowired
    public BrandService service;

    @Autowired
    public CategoryService categoryService;

    // List all brands using normal way
//    @GetMapping("/brands")
//    public String listAllBrands(Model model) {
//        List<Brand> listBrands =  service.getAllBrands();
//
//        model.addAttribute("listBrands", listBrands);
//        return "brand/brands";
//    }

    // List first page of brands using Page
    @GetMapping("/brands")
    public String listFirstPageBrands(Model model) {
        listBrandByPage(1, model);
        return "brand/brands";
    }

    // Create new brand
    @GetMapping("/brands/new")
    public String createBrand(Model model) {
        Brand newBrand = new Brand();
        List<Category> listCategories = categoryService.listAllCategoriesInForm();

        model.addAttribute("brand", newBrand);
        model.addAttribute("pageTitle", "Create New Brand");
        model.addAttribute("listCategories", listCategories);

        return "brand/brand_form";
    }

    @PostMapping("/brands/save")
    public String saveBrand(Brand brand, RedirectAttributes redirectAttributes,
                            @RequestParam(name = "brandLogo")MultipartFile multipartFile) throws IOException {

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        brand.setLogo(fileName);

        Brand savedBrand = service.save(brand);
        String uploadDirectory = "brand-logos/" + savedBrand.getId();

        // Delete all pics before save new one
        FileUploadUtil.cleanDir(uploadDirectory);
        FileUploadUtil.saveFile(uploadDirectory, fileName, multipartFile);

        redirectAttributes.addFlashAttribute("message", "Brand has been saved successfully");
        return "redirect:/brands";
    }

    // Edit existing brand
    @GetMapping("/brands/edit/{id}")
    public String editBrand(@PathVariable(name = "id") Integer id, Model model,
                            RedirectAttributes redirectAttributes) {
        try {
            Brand brand = service.getBrandById(id);
            List<Category> listCategories = categoryService.listAllCategoriesInForm();

            model.addAttribute("brand", brand);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", "Update Existing Brand (ID: " + brand.getId() +")");

            return "/brand/brand_form";
        } catch (BrandNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/brands";
        }
    }

    // Delete Brand By Id
    @GetMapping("/brands/delete/{id}")
    public String deleteBrandById(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            service.deleteBrandById(id);
            redirectAttributes.addFlashAttribute("message", "Delete Brand with ID: " + id + " successfully !");
        } catch (BrandNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/brands";
    }

    // Pagination of Brand - List brands by Page
    @GetMapping("/brands/page/{pageNum}")
    public String listBrandByPage(@PathVariable(name = "pageNum") Integer pageNum,
                                  Model model) {
        Page<Brand> pageBrand = service.getBrandByPage(pageNum-1);

        List<Brand> listBrands = pageBrand.getContent();

        // calculate items in page
        long totalItems = pageBrand.getTotalElements();
        long startCount = (long) (pageNum - 1) *service.BRANDS_PER_PAGE + 1;
        long endCount = startCount + service.BRANDS_PER_PAGE -1;
        if (endCount > totalItems) {
            endCount = totalItems;
        }
        long totalPage = pageBrand.getTotalPages();
        // Pass to View page by model
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPage", totalPage);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("listBrands", listBrands);

        return "/brand/brands";
    }

}
