package com.dita.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dita.domain.Recipe;
import com.dita.domain.RecipeSub;

@Repository
public interface RecipeSubRepository extends JpaRepository<RecipeSub, Long> {
    long countByUserId(String userId);
    Page<RecipeSub> findByUserId(String userId, Pageable pageable);
    List<Recipe> findByRecipeIdIn(List<Long> recipeIds);
}
