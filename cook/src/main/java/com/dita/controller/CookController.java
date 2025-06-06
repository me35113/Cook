package com.dita.controller;

import java.util.List;

//기존 import 문들 아래에 추가
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dita.domain.Category;
import com.dita.domain.Member;
import com.dita.domain.Recipe;
import com.dita.domain.Recipe_ingredient;
import com.dita.domain.Recipe_step;
import com.dita.persistence.CategoryRepository;
import com.dita.persistence.MemberRepository;
import com.dita.persistence.RecipeRepository;
import com.dita.persistence.Recipe_ingredientRepository;
import com.dita.persistence.Recipe_stepRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;

@Controller
public class CookController {

    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private Recipe_stepRepository recipeStepRepository;
    
    @Autowired
    private Recipe_ingredientRepository recipeIngredientRepository;

    
    // 레시피 등록 폼 페이지
    @GetMapping("/registration")
    public String registrationForm(Model model) {
        model.addAttribute("recipe", new Recipe());
        return "registration";
    }    
    
    // 레시피 등록 처리
    @PostMapping("/register")
    public String processRegistration(Recipe recipe, HttpSession session) {
        // 세션에서 로그인된 회원 ID 꺼내기 (로그인 성공 시 세션에 저장된 키)
        String userId = (String) session.getAttribute("idKey");
        recipe.setUserId(userId);

        recipeRepository.save(recipe);
        return "redirect:/recipe/list";
    }
    
    @PostMapping("/registration")
    public String processRegistration(@ModelAttribute Recipe recipe, 
                                    @RequestParam(required = false) String[] category,
                                    @RequestParam(required = false) String steps,
                                    @RequestParam(required = false) String ingredients,
                                    @RequestParam(required = false) MultipartFile complete_img,
                                    HttpSession session) {
        try {
            // 세션에서 로그인된 회원 ID 가져오기
            String userId = (String) session.getAttribute("idKey");
            if (userId == null) {
                userId = "abcd"; // 임시 사용자 ID
            }
            recipe.setUserId(userId);
            
            // 기본값 설정
            if (recipe.getStatus() == null) recipe.setStatus(null);
            if (recipe.getViews() == null) recipe.setViews(0);
            
            // 완성사진 파일 처리
            if (complete_img != null && !complete_img.isEmpty()) {
                String uploadDir = System.getProperty("user.dir") + "/uploads/completed/";
                File directory = new File(uploadDir);
                if (!directory.exists()) directory.mkdirs();
                
                String originalFilename = complete_img.getOriginalFilename();
                String savedFilename = System.currentTimeMillis() + "_" + originalFilename;
                
                File savedFile = new File(uploadDir + savedFilename);
                complete_img.transferTo(savedFile);
                
                recipe.setCompletionUrl("/uploads/completed/" + savedFilename);
            }
            
            // 레시피 저장
            Recipe savedRecipe = recipeRepository.save(recipe);
            
            // 카테고리 정보 저장
            if (category != null && category.length > 0) {
                for (String type : category) {
                    Category categoryEntity = new Category();
                    categoryEntity.setRecipeId(savedRecipe.getRecipeId());
                    categoryEntity.setType(type);
                    categoryRepository.save(categoryEntity);
                }
            }
            
            // 재료 정보 저장
            if (ingredients != null && !ingredients.trim().isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode ingredientsArray = objectMapper.readTree(ingredients);
                
                for (int i = 0; i < ingredientsArray.size(); i++) {
                    JsonNode ingredientNode = ingredientsArray.get(i);
                    String ingredientName = ingredientNode.get("ingredient_name").asText();
                    
                    if (ingredientName != null && !ingredientName.trim().isEmpty()) {
                        Recipe_ingredient recipeIngredient = new Recipe_ingredient();
                        recipeIngredient.setRecipeId(savedRecipe.getRecipeId());
                        recipeIngredient.setGroupName(ingredientNode.get("group_name").asText());
                        recipeIngredient.setIngredientName(ingredientName);
                        recipeIngredient.setQuantity1(ingredientNode.get("quantity1").asText());
                        recipeIngredient.setQuantity2(ingredientNode.get("quantity2").asText());
                        recipeIngredient.setQuantity3(ingredientNode.get("quantity3").asText());
                        
                        recipeIngredientRepository.save(recipeIngredient);
                    }
                }
            }
            
            // 요리순서 정보 저장
            if (steps != null && !steps.trim().isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode stepsArray = objectMapper.readTree(steps);
                
                for (int i = 0; i < stepsArray.size(); i++) {
                    JsonNode stepNode = stepsArray.get(i);
                    String explanation = stepNode.get("explanation").asText();
                    
                    if (explanation != null && !explanation.trim().isEmpty()) {
                        Recipe_step recipeStep = new Recipe_step();
                        recipeStep.setRecipeId(savedRecipe.getRecipeId());
                        recipeStep.setStepId(i + 1);
                        recipeStep.setExplanation(explanation);
                        
                        String imageKey = stepNode.get("image").asText();
                        if (imageKey != null && !imageKey.trim().isEmpty()) {
                            recipeStep.setImage(imageKey);
                        }
                        
                        recipeStepRepository.save(recipeStep);
                    }
                }
            }
            
            return "redirect:/registration";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "registration";
        }
    }
}