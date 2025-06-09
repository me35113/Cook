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

    // 제목에 특정 단어가 포함된 레시피 검색
    List<Recipe> findByTitleContaining(String keyword);

    // 특정 사용자가 작성한 레시피
    List<Recipe> findByUserId(String userId);

    // 상태가 true인 레시피만
    List<Recipe> findByStatusTrue();

    // 특정 나라/종류/검색어 조건으로 필터링 (옵션)
    List<Recipe> findByTitleContainingAndStatusTrue(String keyword);

    // 페이징
    //Page<Recipe> findAll(Pageable pageable);
    
    int countByUserId(String userId);
    Page<Recipe> findByUserId(String userId, Pageable pageable); // 페이징 추가
	
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
    
    
    
}
