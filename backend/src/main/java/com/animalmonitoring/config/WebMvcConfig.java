package com.animalmonitoring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Serves uploaded evidence images statically under /uploads/**.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${ai.image.storage-path:uploads/detections}")
    private String storagePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path path = Paths.get(storagePath).toAbsolutePath().normalize();
        String uploadUrl = "file:" + path.toString() + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadUrl, "file:uploads/", "file:uploads/detections/");
    }
}
