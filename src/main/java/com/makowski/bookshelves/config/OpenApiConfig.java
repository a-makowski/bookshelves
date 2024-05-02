package com.makowski.bookshelves.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
            .title("BookShelves API")
            .description("API for the creation and maintenance of a database of books and their ratings, and of users and their libraries.")
            .version("v1.0"));
    }
}
