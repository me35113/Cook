package com.dita.persistence;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dita.domain.Recipe;
import com.dita.domain.RecipeSub;

@Repository
public interface RecipeSubRepository extends JpaRepository<RecipeSub, Long> {

    @Query("SELECT COUNT(rs) FROM RecipeSub rs WHERE rs.recipeId = :recipeId AND rs.state = 1")
    Long countLiked(@Param("recipeId") int recipeId, int state);
    
    long countByUserId(String userId);
    Page<RecipeSub> findByUserId(String userId, Pageable pageable);
    List<Recipe> findByRecipeIdIn(List<Long> recipeIds);
}
