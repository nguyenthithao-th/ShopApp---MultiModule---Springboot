package com.example.shopauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example")
public class ShopAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopAuthApplication.class, args);
    }

}
