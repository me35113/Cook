package com.dita.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dita.domain.Recipe_ingredient;

public interface Recipe_ingredientRepository extends JpaRepository<Recipe_ingredient, Integer> {

}
