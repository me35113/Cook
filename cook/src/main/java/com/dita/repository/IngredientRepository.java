package com.dita.repository;

import com.dita.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, String> {
    // 재료 이름으로 재료 정보 조회
    Ingredient findByIngredientName(String ingredientName);
}
