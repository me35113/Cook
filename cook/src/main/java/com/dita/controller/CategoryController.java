package com.dita.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dita.domain.Category;
import com.dita.domain.Recipe;
import com.dita.persistence.CategoryRepository;
import com.dita.persistence.RecipeRepository;
import com.dita.persistence.RecipeSubRepository;
import com.dita.vo.PageMaker;
import com.dita.vo.PageVO;

@Controller
public class CategoryController {

	@Autowired
	private RecipeRepository recipeRepository;
	
	@Autowired
	private RecipeSubRepository recipeSubRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
    

	
    /*
    @GetMapping("/category")
    public String showCategory(
    		@RequestParam(value = "keyword", required = false) String keyword,
    		PageVO vo, Model model) {

        Pageable pageable = vo.makePageable(0, "recipeId"); // 정렬기준 recipeId
        Page<Recipe> result = recipeRepository.findAll(pageable);

        // 좋아요 수 저장
        Map<Integer, Long> likeCounts = new HashMap<>();
        for (Recipe recipe : result.getContent()) {
            Long count = recipeSubRepository.countLiked(recipe.getRecipeId(), 1);
            likeCounts.put(recipe.getRecipeId(), count);
        }
        

        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("recipes", result.getContent());
        model.addAttribute("result", new PageMaker<>(result)); // 페이징 정보
        model.addAttribute("totalCount", result.getTotalElements());
        
        
        return "category";
    }
    */

	@GetMapping("/category")
	public String showCategory(
	        @RequestParam(value = "type", required = false) List<String> types,
	        @RequestParam(value = "food_group_id", required = false) List<String> foodGroupIdsStr,
	        @RequestParam(value = "country", required = false) List<String> countries,
	        PageVO vo, Model model) {

	    Pageable pageable = vo.makePageable(0, "recipeId");

	    // 필터 존재 여부
	    boolean hasType = types != null && !types.isEmpty();
	    boolean hasFoodGroup = foodGroupIdsStr != null && !foodGroupIdsStr.isEmpty();
	    boolean hasCountry = countries != null && !countries.isEmpty();

	    // food_group_id는 Integer 리스트로 변환
	    List<Integer> foodGroupIds = null;
	    if (hasFoodGroup) {
	        foodGroupIds = foodGroupIdsStr.stream().map(Integer::parseInt).toList();
	    }

	    List<Recipe> recipeList;

	    // 필터 조건이 하나라도 존재하면 category 테이블 기준으로 recipe_id 가져오기
	    if (hasType || hasFoodGroup || hasCountry) {
	        // 여기서 로그 확인
	        System.out.println("▶ [DEBUG] types = " + types);
	        System.out.println("▶ [DEBUG] foodGroupIds = " + foodGroupIds);
	        System.out.println("▶ [DEBUG] countries = " + countries);
	        System.out.println("▶ [DEBUG] hasType = " + hasType);
	        System.out.println("▶ [DEBUG] hasFoodGroup = " + hasFoodGroup);
	        System.out.println("▶ [DEBUG] hasCountry = " + hasCountry);
	        
	        List<Integer> recipeIds = categoryRepository.findMatchingRecipeIds(
	                hasType ? types : null,
	                hasFoodGroup ? foodGroupIds : null,
	                hasCountry ? countries : null,
	                hasType, hasFoodGroup, hasCountry
	        );

	        System.out.println("✅ 필터링된 recipeIds: " + recipeIds);

	        if (recipeIds == null || recipeIds.isEmpty()) {
	            recipeList = List.of(); // 결과 없음
	        } else {
	            recipeList = recipeRepository.findByRecipeIdIn(recipeIds);
	            recipeList.sort(Comparator.comparing(Recipe::getRecipeId).reversed());
	        }
	    } else {
	        // 필터가 없으면 전체 조회 (기존 방식)
	        Page<Recipe> result = recipeRepository.findAll(pageable);
	        model.addAttribute("likeCounts", getLikeCounts(result.getContent()));
	        model.addAttribute("recipes", result.getContent());
	        model.addAttribute("result", new PageMaker<>(result));
	        model.addAttribute("totalCount", result.getTotalElements());
	        return "category";
	    }

	    // 페이징
	    int start = (int) pageable.getOffset();
	    int end = Math.min(start + pageable.getPageSize(), recipeList.size());
	    List<Recipe> pagedList = recipeList.subList(start, end);
	    Page<Recipe> page = new PageImpl<>(pagedList, pageable, recipeList.size());
	    
	    model.addAttribute("likeCounts", getLikeCounts(pagedList));
	    model.addAttribute("recipes", pagedList);
	    model.addAttribute("result", new PageMaker<>(page));
	    model.addAttribute("totalCount", recipeList.size());

	    return  "category";
	}

	// 좋아요 수 계산
	private Map<Integer, Long> getLikeCounts(List<Recipe> recipes) {
	    Map<Integer, Long> likeCounts = new HashMap<>();
	    for (Recipe recipe : recipes) {
	        Long count = recipeSubRepository.countLiked(recipe.getRecipeId(), 1);
	        likeCounts.put(recipe.getRecipeId(), count);
	    }
	    return likeCounts;
	}

}
