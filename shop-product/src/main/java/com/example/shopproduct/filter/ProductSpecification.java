package com.example.shopproduct.filter;

import com.example.shopproduct.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<Product> hasNameLike(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> priceGreaterThanOrEq(Double minPrice) {
        return (root, query, cb) ->
                minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Product> priceLessThanOrEq(Double maxPrice) {
        return (root, query, cb) ->
                maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }
}