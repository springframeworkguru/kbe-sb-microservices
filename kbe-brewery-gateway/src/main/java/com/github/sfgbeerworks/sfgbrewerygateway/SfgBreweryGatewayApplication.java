package com.github.sfgbeerworks.sfgbrewerygateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SfgBreweryGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SfgBreweryGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {

        return builder.routes()
                .route("beer-service", r -> r.path("/api/v1/beer/*", "/api/v1/beerUpc/*")
                        .uri("http://beer-service:8080"))
                .route("inventory-service", r -> r.path("/api/v1/beer/*/inventory")
                        .uri("http://inventory-service:8082"))
                .route("order-service", r -> r.path(("/api/v1/customers/**"))
                        .uri("http://order-service:8081"))
                .build();
    }

}
