package com.example.shopapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.example")

public class ShopAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopAppApplication.class, args);
	}

}
