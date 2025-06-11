package com.dita.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dita.domain.Recipe;
import com.dita.persistence.RecipeRepository;
import com.dita.persistence.RecipeService;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeServiceImpl(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public List<Recipe> getBestRecipes() {
        // likes 컬럼이 없으므로 최신순 5개로 대체
        return recipeRepository.findTop5ByOrderByRecDateDesc();
    }

    @Override
    public List<Recipe> getNewRecipes() {
        return recipeRepository.findTop5ByOrderByRecDateDesc();
    }
    @Override
    public List<Recipe> getRandomRecipes() {
        return recipeRepository.findRandom5();
    }
}
