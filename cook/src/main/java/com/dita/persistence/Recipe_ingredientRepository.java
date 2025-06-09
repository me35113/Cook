package com.dita.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dita.domain.RecipeIngredient;

public interface Recipe_ingredientRepository extends JpaRepository<RecipeIngredient, Integer> {

}
