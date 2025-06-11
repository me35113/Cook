package com.dita.persistence;

import java.util.List;
import com.dita.domain.Recipe;

public interface RecipeService {
    List<Recipe> getBestRecipes();  // 인기 레시피 (현재는 최신순으로 대체)
    List<Recipe> getNewRecipes();   // 최신 레시피
    List<Recipe> getRandomRecipes(); // 랜덤 레시피
}
