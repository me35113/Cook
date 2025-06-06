package com.dita.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dita.domain.RecipeIngredient;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Integer>{

	List<RecipeIngredient> findByRecipeIdOrderByRecipeIngredientGroupIdAscRecipeIngredientIdAsc(Integer recipeId); // ✅ 정상 작동

}
