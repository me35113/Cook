package com.dita.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "category")
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;
    
    @Column(name = "recipe_id")
    private Integer recipeId;
    
    @Column(name = "food_group_id")
    private Integer foodGroupId;
    
    @Column(name = "country", length = 20)
    private String country;
    
    @Column(name = "cooking_method", length = 20)
    private String cookingMethod;
    
    @Column(name = "type", length = 20)
    private String type;
    
}