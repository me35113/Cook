package com.dita.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "recipe_ingredient")
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_ingredient_id")  // DB 컬럼명 명시
    private Integer recipeIngredientId;

    @Column(name = "recipe_id")
    private Integer recipeId;

    @Column(name = "recipe_ingredient_group_id")
    private Integer recipeIngredientGroupId;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "ingredient_name")
    private String ingredientName;

    @Column(name = "quantity1")
    private String quantity1;

    @Column(name = "quantity2")
    private String quantity2;

    @Column(name = "quantity3")
    private String quantity3;
}

