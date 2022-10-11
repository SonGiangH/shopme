package com.shopme.admin.product;

import com.shopme.admin.brand.BrandService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String saveProduct(Product product,RedirectAttributes redirectAttributes) {
        service.save(product);

        redirectAttributes.addFlashAttribute("message", "Product has been saved successfully");
        return "redirect:/products";
    }
}
