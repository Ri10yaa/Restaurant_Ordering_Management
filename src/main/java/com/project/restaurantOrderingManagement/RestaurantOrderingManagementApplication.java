package com.project.restaurantOrderingManagement;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@EnableMongoRepositories
@EnableRedisRepositories
public class RestaurantOrderingManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestaurantOrderingManagementApplication.class, args);
	}

}
