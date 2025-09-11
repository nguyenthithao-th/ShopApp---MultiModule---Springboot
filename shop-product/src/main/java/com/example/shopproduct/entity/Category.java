package com.example.shopproduct.entity;


import com.example.shopcore.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Category extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();
}
