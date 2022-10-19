package com.shopme.admin.product;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.exception.BrandNotFoundException;
import com.shopme.admin.exception.ProductNotFoundException;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ProductController {

    @Autowired
    private ProductService service;

    @Autowired
    private BrandService brandService;

    @GetMapping("/products")
    public String listAllProducts(Model model) {
        List<Product> listProducts = service.findAllProducts();
        model.addAttribute("listProducts", listProducts);

        return "/product/products";
    }

    // Create new product
    @GetMapping("/products/new")
    public String createNewProduct(Model model) {
        // Create new product
        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);

        // Get all the brands in DB
        List<Brand> listBrands = brandService.getAllBrands();

        model.addAttribute("product", product);
        model.addAttribute("listBrands", listBrands);
        model.addAttribute("pageTitle", "Create New Product");
        return "/product/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, RedirectAttributes redirectAttributes,
                              @RequestParam(name = "productMainImage")MultipartFile mainImageMultipartFile,
                              @RequestParam(name = "extraImage") MultipartFile[] extraImageMultipart,
                              @RequestParam(name="detailNames", required = false) String[] detailNames,
                              @RequestParam(name = "detailValues", required = false) String[] detailValues) throws IOException {
            setMainImageName(mainImageMultipartFile, product);
            setExtraImageName(extraImageMultipart, product);
            setProductDetail(detailNames, detailValues, product);

            Product savedProduct = service.save(product);

            // save upload images
            savedUploadImages(mainImageMultipartFile,extraImageMultipart, savedProduct);


            redirectAttributes.addFlashAttribute("message", "Product has been saved successfully");
            return "redirect:/products";
    }

    private void setProductDetail(String[] detailNames, String[] detailValues, Product product) {
        if (detailNames == null || detailNames.length == 0) return;

        for (int count =0; count < detailNames.length; count++) {
            String name = detailNames[count];
            String value = detailValues[count];
            if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetails(name, value);
            }
        }
    }

    private void savedUploadImages(MultipartFile mainImageMultipartFile, MultipartFile[] extraImageMultipart, Product savedProduct) throws IOException {

        // save main Image files
        if (!mainImageMultipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());
            String uploadDir = "../product-images/" + savedProduct.getId();
            // Clear all older pics before upload
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipartFile);
        }

        // save extra images files
        if (extraImageMultipart.length > 0) {
            String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
            for (MultipartFile multipartFile : extraImageMultipart) {
                // if multipart empty -> continue for loop
                if (multipartFile.isEmpty()) continue;

                String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
            }
        }
    }

    private void setMainImageName(MultipartFile mainImageMultipartFile, Product product) {
        if (!mainImageMultipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());
            product.setMainImage(fileName);
        }
    }

    private void setExtraImageName(MultipartFile[] extraImageMultipart, Product product) {
        if (extraImageMultipart.length > 0) {
            for (MultipartFile multipartFile : extraImageMultipart) {
                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    product.addExtraImages(fileName);
                }
            }
        }
    }
    // Update enabled status
    @GetMapping("/products/{id}/enable/{status}")
    public String updateEnabledProduct(@PathVariable(name = "id") Integer id,
                                       @PathVariable(name = "status") boolean enabled,
                                       RedirectAttributes redirectAttributes) {
        service.updateEnableStatus(id, enabled);
        String action = enabled ? "Enable" : "Disable";
        redirectAttributes.addFlashAttribute("message", action + "Product (ID: " + id + ")");
        return "redirect:/products";
    }

    // Delete Product By Id
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Integer id,
                                RedirectAttributes redirectAttributes) {
        try {
            service.deleteProductById(id);
            // clean folder contains images
            String uploadExtraDir = "../product-images/" + id + "/extras";
            String uploadProductDir = "../product-images/" + id;
            FileUploadUtil.removeDir(uploadExtraDir);
            FileUploadUtil.removeDir(uploadProductDir);

            redirectAttributes.addFlashAttribute("message", "Delete product with ID: " + id + "successfully !");
        } catch (ProductNotFoundException | IOException e) {
            redirectAttributes.addFlashAttribute("message",e.getMessage());
        }
        return "redirect:/products";
    }

    // Edit Product By Id
    @GetMapping("/products/edit/{id}")
    public String editProduct(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
                              Model model) {
        try {
            // get product -> add to model
            Product product = service.getProductById(id);
            // get brands  -> add to model
            List<Brand> listBrands = brandService.getAllBrands();
            // get number of total extra images to push to model
            Integer numberTotalExtraImages = product.getImages().size();

            model.addAttribute("product", product);
            model.addAttribute("listBrands",listBrands);
            model.addAttribute("pageTitle", "Edit Existing Product (ID: " + id + ")");
            model.addAttribute("totalExtraImages",numberTotalExtraImages);
            return "/product/product_form";
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/products";
        }
    }
}
