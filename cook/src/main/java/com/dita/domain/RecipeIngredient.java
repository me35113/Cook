package com.dita.domain;

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
	private Integer recipeIngredientId;
	
	private Integer recipeId;
	
	private Integer recipeIngredientGroupId;

    private String groupName;

    private String ingredientName;
	
	private String quantity1;
	private String quantity2;
	private String quantity3;
	
}
