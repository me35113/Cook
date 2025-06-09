package com.dita.controller;

import com.dita.domain.Comment;
import com.dita.domain.Ingredient;
import com.dita.domain.Member;
import com.dita.domain.Recipe;
import com.dita.domain.RecipeSub;
import com.dita.domain.Store;
import com.dita.persistence.CommentRepository;
import com.dita.persistence.IngredientRepository;
import com.dita.persistence.MemberRepository;
import com.dita.persistence.MemberSubRepository;
import com.dita.persistence.RecipeRepository;
import com.dita.persistence.RecipeSubRepository;
import com.dita.persistence.StoreRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class MyRecipeController {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private MemberSubRepository memberSubRepository;
    
    @Autowired
    private RecipeSubRepository recipeSubRepository;
    private final String CURRENT_USER_ID = "1111";
    
    private final StoreRepository storeRepository;
    private final IngredientRepository ingredientRepository;
    
    
    @GetMapping("/myrecipes")
    public String getMyRecipes(Model model) {
        // 레시피 리스트
    	
        List<Recipe> recipes = recipeRepository.findByUserId(CURRENT_USER_ID);
        model.addAttribute("recipes", recipes);

        // 현재 유저 아이디
        model.addAttribute("currentUserId", CURRENT_USER_ID);

        // 회원 정보 조회
        Optional<Member> memberOpt = memberRepository.findById(CURRENT_USER_ID);
        if (memberOpt.isPresent()) {
            model.addAttribute("member", memberOpt.get());
        } else {
            // 회원 정보 없을 때 예외 처리 혹은 기본값 설정 가능
            model.addAttribute("member", null);
        }

        return "myrecipes";  // templates/myrecipes.html
    }
    
    @GetMapping("/my_recipes")
    public String getMyRecipesAlias(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "6") int size,
            HttpSession session, Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("recipeId").descending());

        // 페이징 처리된 레시피 리스트 가져오기
        Page<Recipe> recipePage = recipeRepository.findByUserId(CURRENT_USER_ID, pageable);
        model.addAttribute("recipePage", recipePage);

        // 페이징에 표시할 레시피 리스트(현재 페이지의 컨텐츠)
        model.addAttribute("recipes", recipePage.getContent());

        // 현재 유저 아이디
        model.addAttribute("currentUserId", CURRENT_USER_ID);

        // 회원 정보 조회
        Optional<Member> memberOpt = memberRepository.findById(CURRENT_USER_ID);
        model.addAttribute("member", memberOpt.orElse(null));
        session.setAttribute("CURRENT_USER_ID", CURRENT_USER_ID);
        String currentUserId = (String) session.getAttribute("CURRENT_USER_ID");

        // 회원 정보 조회
        Member member = memberRepository.findById(CURRENT_USER_ID).orElse(null);
        model.addAttribute("member", member);
        model.addAttribute("currentUserId", CURRENT_USER_ID);

        // 구독 수 (내가 구독한 사람 수)
        int subscribeCount = memberSubRepository.countBySubUserAndState(CURRENT_USER_ID, 1);
        // 구독자 수 (나를 구독한 사람 수)
        int subscribedCount = memberSubRepository.countBySubedUserAndState(CURRENT_USER_ID, 1);

        model.addAttribute("subscribeCount", subscribeCount);
        model.addAttribute("subscribedCount", subscribedCount);
        
        if (currentUserId != null) {
            // 총 레시피 개수 조회
            int recipeCount = recipeRepository.countByUserId(currentUserId);
            model.addAttribute("recipeCount", recipeCount);
        } else {
            model.addAttribute("recipeCount", 100);
        }

        return "my_recipes";  // templates/my_recipes.html
    }

    
    @GetMapping("/my_recipes_paginated")
    public String getMyRecipesPaginated(
            @RequestParam(value = "page", defaultValue = "1") int page,
            HttpSession session,
            Model model) {

        String currentUserId = CURRENT_USER_ID;
        session.setAttribute("CURRENT_USER_ID", currentUserId);
        model.addAttribute("currentUserId", currentUserId);

        Optional<Member> memberOpt = memberRepository.findById(currentUserId);
        model.addAttribute("member", memberOpt.orElse(null));

        Pageable pageable = PageRequest.of(page - 1, 6, Sort.by("recipeId").descending());
        Page<Recipe> recipesPage = recipeRepository.findByUserId(currentUserId, pageable);

        model.addAttribute("recipesPage", recipesPage);
        model.addAttribute("recipes", recipesPage.getContent());
        model.addAttribute("recipeCount", recipesPage.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", recipesPage.getTotalPages());

        return "my_recipes_paginated";
    }
    
    @GetMapping("/my_comment")
    public String getMyCommentsPaginated(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "6") int size,
            HttpSession session,
            Model model) {

        if (page < 1) {
            page = 1;
        }

        String currentUserId = CURRENT_USER_ID;  // 실제론 로그인된 사용자 ID로 대체
        session.setAttribute("CURRENT_USER_ID", currentUserId);
        model.addAttribute("currentUserId", currentUserId);

        // 회원 정보 조회
        Optional<Member> memberOpt = memberRepository.findById(currentUserId);
        model.addAttribute("member", memberOpt.orElse(null));

        // 페이지 요청 생성 (page-1: 1부터 시작하는 페이지 번호를 0부터 시작하는 인덱스로 변환)
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("commentId").descending());
        Page<Comment> commentPage = commentRepository.findByUserId(currentUserId, pageable);

        // 댓글-레시피 제목, 작성자명 설정 (중복된 코드 제거 가능)
        commentPage.getContent().forEach(comment -> {
            if (comment.getRecipeId() != null) {
                recipeRepository.findById(comment.getRecipeId()).ifPresentOrElse(
                    recipe -> {
                        comment.setRecipeTitle(recipe.getTitle());
                        memberRepository.findById(recipe.getUserId()).ifPresent(writer -> {
                            comment.setRecipeWriterName(writer.getName() + "(" + recipe.getUserId() + ")");
                        });
                    },
                    () -> {
                        comment.setRecipeTitle("삭제된 레시피");
                        comment.setRecipeWriterName("알 수 없음");
                    });
            } else {
                comment.setRecipeTitle("삭제된 레시피");
                comment.setRecipeWriterName("알 수 없음");
            }
        });

        model.addAttribute("comments", commentPage.getContent());
        model.addAttribute("commentCount", commentPage.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", commentPage.getTotalPages());
        model.addAttribute("commentPage", commentPage);

        return "my_comment";  // templates/my_comment.html
    }
    
    @GetMapping("/my_likes")
    public String myLikes(Model model,
                          @RequestParam(value = "page", defaultValue = "1") int page,
                          HttpSession session) {
        int pageSize = 6;
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        // 현재 로그인한 유저 ID 가져오기
        String userId = CURRENT_USER_ID;

        // 🔹 member 정보 가져오기
        Optional<Member> optionalMember = memberRepository.findByUserId(userId);
        optionalMember.ifPresent(member -> model.addAttribute("member", member));
        model.addAttribute("currentUserId", userId);

        // 좋아요한 레시피 목록
        Page<RecipeSub> subscriptions = recipeSubRepository.findByUserId(userId, pageable);
        List<Integer> recipeIds = subscriptions.getContent().stream()
                                               .map(RecipeSub::getRecipeId)
                                               .collect(Collectors.toList());

        List<Object[]> resultList = recipeRepository.findRecipesWithUserNameByIds(recipeIds);

        List<Map<String, Object>> likedRecipes = new ArrayList<>();
        for (Object[] row : resultList) {
            Recipe recipe = (Recipe) row[0];
            String userName = (String) row[1];

            Map<String, Object> map = new HashMap<>();
            map.put("recipe", recipe);
            map.put("userName", userName);
            likedRecipes.add(map);
        }

        model.addAttribute("likedRecipes", likedRecipes);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", subscriptions.getTotalPages());

        return "my_likes";
    }

    public MyRecipeController(StoreRepository storeRepository, IngredientRepository ingredientRepository) {
        this.storeRepository = storeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @GetMapping("/manage_ingredients")
    public String getUserIngredients(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "6") int size,
            Model model) {

        String currentUserId = CURRENT_USER_ID;
        model.addAttribute("currentUserId", currentUserId);
        Optional<Member> memberOpt = memberRepository.findById(currentUserId);
        model.addAttribute("member", memberOpt.orElse(null));

        Pageable pageable = PageRequest.of(page, size);
        Page<Store> storePage = storeRepository.findByUserIdWithIngredient(currentUserId, pageable);

        if (storePage == null || storePage.isEmpty()) {
            model.addAttribute("hasIngredients", false);
            return "manage_ingredients";
        }

        List<UserIngredientInfo> ingredientInfos = storePage.getContent().stream()
            .map(store -> {
                Ingredient ingredient = ingredientRepository.findByIngredientName(store.getIngredientName());
                return new UserIngredientInfo(
                    store.getIngredientName(),
                    ingredient.getMethod(),
                    store.getStoreCreate(),
                    store.getExpirationDate()
                );
            })
            .collect(Collectors.toList());

        model.addAttribute("hasIngredients", true);
        model.addAttribute("ingredientInfos", ingredientInfos);
        model.addAttribute("ingredientPage", storePage);

        return "manage_ingredients";
    }




    // DTO 내부 클래스
    public static class UserIngredientInfo {
        private String ingredientName;
        private String method;
        private String storeCreate;
        private java.sql.Date expirationDate;

        public UserIngredientInfo(String ingredientName, String method, String storeCreate, java.sql.Date expirationDate) {
            this.ingredientName = ingredientName;
            this.method = method;
            this.storeCreate = storeCreate;
            this.expirationDate = expirationDate;
        }
        // getters
        public String getIngredientName() { return ingredientName; }
        public String getMethod() { return method; }
        public String getStoreCreate() { return storeCreate; }
        public java.sql.Date getExpirationDate() { return expirationDate; }
    }

    @GetMapping("/ingredients")
    public String getIngredientsPage(@RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "size", defaultValue = "6") int size,
                                     Model model) {

        String currentUserId = CURRENT_USER_ID;
        model.addAttribute("currentUserId", currentUserId);
        Optional<Member> memberOpt = memberRepository.findById(currentUserId);
        model.addAttribute("member", memberOpt.orElse(null));

        Pageable pageable = PageRequest.of(page, size);
        Page<Store> storePage = storeRepository.findByUserIdWithIngredient2(currentUserId, pageable);

        boolean hasIngredients = !storePage.isEmpty();
        model.addAttribute("ingredientPage", storePage);
        model.addAttribute("hasIngredients", hasIngredients);

        return "ingredients";
    }

    @PostMapping("/save_ingredients")
    @ResponseBody
    public ResponseEntity<?> saveIngredients(@RequestBody List<Store> ingredients, HttpSession session) {
        String userId = CURRENT_USER_ID;
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }

        // 1) 기존 userId에 해당하는 store 데이터 모두 삭제
        List<Store> existingStores = storeRepository.findByUserId(userId);
        storeRepository.deleteAll(existingStores);

        // 2) 넘어온 리스트에 userId 세팅 후 저장
        for (Store store : ingredients) {
            store.setUserId(userId);
            store.setStoreId(0); // 새로 저장할 때 ID 초기화
            storeRepository.save(store);
        }

        return ResponseEntity.ok("저장 완료");
    }

    
    @GetMapping("/ingredients_add")
    public String showIngredients(Model model) {
        List<Store> ingredientInfos = storeRepository.findByUserId(CURRENT_USER_ID); // store 테이블 조회
        model.addAttribute("ingredientInfos", ingredientInfos);
        return "ingredients_add";
    }
    
    @GetMapping("/personalized_recipes")
    public String getPersonalizedRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            Model model) {

        String currentUserId = CURRENT_USER_ID; // 로그인된 사용자

        // 1. 로그인된 사용자의 보유 재료 조회
        List<String> userIngredients = storeRepository.findIngredientNamesByUserId(currentUserId);

        // 2. 해당 재료를 포함하는 recipe_id 조회
        List<Integer> recipeIds = recipeRepository.findDistinctRecipeIdsByIngredientNames(userIngredients);

        // 3. 해당 recipe_id 기반으로 Recipe 조회 (페이지네이션)
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipePage = recipeRepository.findByRecipeIdIn(recipeIds, pageable);

        model.addAttribute("recipes", recipePage.getContent());
        model.addAttribute("recipePage", recipePage);
        model.addAttribute("currentUserId", currentUserId);

        return "personalized_recipes";
    }


}