//package com.example.practice.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        // Handle single level routes (e.g., "/about", "/items")
//        registry.addViewController("/{spring:[a-zA-Z0-9-]+}")
//                .setViewName("forward:/index.html");
//
//        // Handle two-level routes (e.g., "/about/123")
//        registry.addViewController("/{spring:[a-zA-Z0-9-]+}/{id:[0-9]+}")
//                .setViewName("forward:/index.html");
//
//        // Handle deep nested routes (e.g., "/user/profile/details")
//        registry.addViewController("/**")
//                .setViewName("forward:/index.html");
//    }
//}
