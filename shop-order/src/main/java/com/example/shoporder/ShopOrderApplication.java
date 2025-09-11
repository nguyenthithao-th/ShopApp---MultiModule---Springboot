package com.example.shoporder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.shopinventory")
public class ShopOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopOrderApplication.class, args);
    }

}
