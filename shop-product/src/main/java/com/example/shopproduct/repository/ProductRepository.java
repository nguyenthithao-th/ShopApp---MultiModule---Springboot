package com.example.shopproduct.repository;

import com.example.shopproduct.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByCategoryIdAndIsDeletedFalse(Long categoryId);

    Page<Product> findAllByIsDeletedFalse(Specification<Product> spec, Pageable pageable);

    List<Product> findAllByIsDeletedFalse();

}



