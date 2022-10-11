package com.shopme.admin;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // show (or expose) directory url to web browser
        exposeDirectory(registry);
    }

    private void exposeDirectory(ResourceHandlerRegistry registry) {
        // user-photos
        Path uploadDir = Paths.get("user-photos");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/" + "user-photos" + "/**")
                .addResourceLocations("file:/"+ uploadPath + "/");

        // category-photos
        Path categoryUploadDir = Paths.get("../category-photos");
        String categoryUploadPath = categoryUploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/" + "category-photos" + "/**")
                .addResourceLocations("file:/"+ categoryUploadPath + "/");

        // brand-logos
        Path brandUploadDir = Paths.get("brand-logos");
        String brandUploadPath = brandUploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/" + "brand-logos" + "/**")
                .addResourceLocations("file:/" + brandUploadPath + "/");
    }
}
