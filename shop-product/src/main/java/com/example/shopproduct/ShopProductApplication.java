package com.example.shopproduct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.example.shopproduct",
        "com.example.shopcore"
}
)
public class ShopProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopProductApplication.class, args);
    }

}
