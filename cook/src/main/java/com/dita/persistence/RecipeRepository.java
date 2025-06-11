package com.dita.persistence;

import com.dita.domain.Recipe;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {

    List<Recipe> findByTitleContaining(String keyword);

    List<Recipe> findByUserId(String userId);

    List<Recipe> findByStatusTrue();

    List<Recipe> findByTitleContainingAndStatusTrue(String keyword);

    int countByUserId(String userId);

    Page<Recipe> findByUserId(String userId, Pageable pageable);

    @Query("SELECT r, m.name FROM Recipe r JOIN Member m ON r.userId = m.userId WHERE r.recipeId IN :recipeIds")
    List<Object[]> findRecipesWithUserNameByIds(@Param("recipeIds") List<Integer> recipeIds);

    @Query("SELECT DISTINCT r.recipeId FROM RecipeIngredient r WHERE r.ingredientName IN :ingredients")
    List<Integer> findDistinctRecipeIdsByIngredientNames(@Param("ingredients") List<String> ingredients);

    Page<Recipe> findByRecipeIdIn(List<Integer> recipeIds, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE Recipe r SET r.views = r.views + 1 WHERE r.recipeId = :recipeId")
    void incrementViews(@Param("recipeId") Integer recipeId);

    List<Recipe> findByRecipeIdIn(List<Integer> recipeIds);

    Page<Recipe> findAll(Pageable pageable);

    // 최신 레시피 5개 가져오기
    List<Recipe> findTop5ByOrderByRecDateDesc();
    
    // 랜덤 레시피 5개 가져오기 (네이티브 쿼리)
    @Query(value = "SELECT * FROM recipe ORDER BY RAND() LIMIT 5", nativeQuery = true)
    List<Recipe> findRandom5();

}
