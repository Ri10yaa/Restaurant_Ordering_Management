package com.project.restaurantOrderingManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.project.restaurantOrderingManagement.repositories")
public class RestaurantOrderingManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestaurantOrderingManagementApplication.class, args);
	}

}
