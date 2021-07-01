package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourceConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/articles/**")
                .addResourceLocations("file:///src/main/resources/images/articles/");

        registry.addResourceHandler("/images/partners/**")
                .addResourceLocations("file:///src/main/resources/images/partners/");

        registry.addResourceHandler("/images/spaces/**")
                .addResourceLocations("file:///src/main/resources/images/spaces/");

        registry.addResourceHandler("/images/testimonials/**")
                .addResourceLocations("file:///src/main/resources/images/testimonials/");

        registry.addResourceHandler("/images/covers/**")
                .addResourceLocations("file:///src/main/resources/images/covers/");

        registry.addResourceHandler("/images/images/**")
                .addResourceLocations("file:///src/main/resources/images/images/");

        registry.addResourceHandler("/images/services/**")
                .addResourceLocations("file:///src/main/resources/images/services/");

        registry.addResourceHandler("/images/users/**")
                .addResourceLocations("file:///src/main/resources/images/users/");

        registry.addResourceHandler("/images/events/**")
                .addResourceLocations("file:///src/main/resources/images/events/");
    }
}
