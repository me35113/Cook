package com.dita.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "recipe_ingredient")
public class Recipe_ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_ingredient_id", nullable = false)
    private Integer recipeIngredientId;

    @Column(name = "recipe_id")
    private Integer recipeId;

    @Column(name = "recipe_ingredient_group_id")
    private Integer recipeIngredientGroupId;

    @Column(name = "group_name", length = 20)
    private String groupName;

    @Column(name = "ingredient_name", length = 20)
    private String ingredientName;

    @Column(name = "quantity1", length = 20)
    private String quantity1;

    @Column(name = "quantity2", length = 20)
    private String quantity2;

    @Column(name = "quantity3", length = 20)
    private String quantity3;
}