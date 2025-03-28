package com.example.Child.Growth.Tracking.configuation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Child Growth Tracking API")
                        .version("1.0")
                        .description("API Documentation for Child Growth Tracking System"));
    }
}