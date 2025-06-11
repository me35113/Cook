package com.dita.controller;

import com.dita.CookApplication;
import com.dita.domain.Comment;
import com.dita.domain.Ingredient;
import com.dita.domain.Member;
import com.dita.domain.MemberSub;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.security.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class MyRecipeController {

    private final CookApplication cookApplication;

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

        // 구독 수 (내가 구독한 사람 수)
        int subscribeCount = memberSubRepository.countBySubUserAndState(CURRENT_USER_ID, 1);
        // 구독자 수 (나를 구독한 사람 수)
        int subscribedCount = memberSubRepository.countBySubedUserAndState(CURRENT_USER_ID, 1);

        model.addAttribute("subscribeCount", subscribeCount);
        model.addAttribute("subscribedCount", subscribedCount);
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

        // 구독 수 (내가 구독한 사람 수)
        int subscribeCount = memberSubRepository.countBySubUserAndState(CURRENT_USER_ID, 1);
        // 구독자 수 (나를 구독한 사람 수)
        int subscribedCount = memberSubRepository.countBySubedUserAndState(CURRENT_USER_ID, 1);

        model.addAttribute("subscribeCount", subscribeCount);
        model.addAttribute("subscribedCount", subscribedCount);
        model.addAttribute("likedRecipes", likedRecipes);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", subscriptions.getTotalPages());

        return "my_likes";
    }

    public MyRecipeController(StoreRepository storeRepository, IngredientRepository ingredientRepository, CookApplication cookApplication) {
        this.storeRepository = storeRepository;
        this.ingredientRepository = ingredientRepository;
        this.cookApplication = cookApplication;
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

        // 구독 수 (내가 구독한 사람 수)
        int subscribeCount = memberSubRepository.countBySubUserAndState(CURRENT_USER_ID, 1);
        // 구독자 수 (나를 구독한 사람 수)
        int subscribedCount = memberSubRepository.countBySubedUserAndState(CURRENT_USER_ID, 1);

        model.addAttribute("subscribeCount", subscribeCount);
        model.addAttribute("subscribedCount", subscribedCount);
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

        // 구독 수 (내가 구독한 사람 수)

        // 구독자 수 (나를 구독한 사람 수)
        int subscribedCount = memberSubRepository.countBySubedUserAndState(CURRENT_USER_ID, 1);
        int subscribeCount = memberSubRepository.countBySubUserAndState(CURRENT_USER_ID, 1);
        System.out.println("subscribeCount = " + subscribeCount);
        model.addAttribute("subscribeCount", subscribeCount);

        model.addAttribute("subscribedCount", subscribedCount);

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

        // 기존 데이터 삭제
        List<Store> existingStores = storeRepository.findByUserId(userId);
        storeRepository.deleteAll(existingStores);

        // 등록 처리
        for (Store store : ingredients) {
            store.setUserId(userId);
            store.setStoreId(0); // 새로 저장할 때 ID 초기화

            // 유통기한 자동 계산
            if ((store.getExpirationDate() == null || store.getExpirationDate().toString().isEmpty())
                && store.getIngredientName() != null && store.getStoreCreate() != null) {

                Ingredient ingredient = ingredientRepository.findByIngredientName(store.getIngredientName());
                if (ingredient != null) {
                    try {
                        int shelfLife = ingredient.getShelfLife(); // 예: 10일

                        // storeCreate (String) → LocalDate
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate storeCreateDate = LocalDate.parse(store.getStoreCreate(), formatter);

                        // 계산된 유통기한
                        LocalDate expirationDate = storeCreateDate.plusDays(shelfLife);

                        // LocalDate → java.sql.Date로 변환하여 저장
                        store.setExpirationDate(java.sql.Date.valueOf(expirationDate));
                    } catch (Exception e) {
                        System.err.println("날짜 변환 또는 계산 실패: " + store.getStoreCreate());
                        e.printStackTrace();
                    }
                }
            }

            storeRepository.save(store);
        }

        return ResponseEntity.ok("저장 완료");
    }


    @PostMapping("/calculate_expiration")
    @ResponseBody
    public String calculateExpiration(@RequestBody Map<String, String> payload) {
        String ingredientName = payload.get("ingredientName");
        String storeCreate = payload.get("storeCreate");

        Ingredient ingredient = ingredientRepository.findByIngredientName(ingredientName);
        if (ingredient == null) return "";

        int shelfLife = ingredient.getShelfLife();
        LocalDate createDate = LocalDate.parse(storeCreate);
        LocalDate expirationDate = createDate.plusDays(shelfLife);
        return expirationDate.toString();
    }

    
    @GetMapping("/ingredients_add")
    public String showIngredients(Model model) {
        List<Store> ingredientInfos = storeRepository.findByUserId(CURRENT_USER_ID); // store 테이블 조회
        String currentUserId = CURRENT_USER_ID;
        model.addAttribute("currentUserId", currentUserId);
        Optional<Member> memberOpt = memberRepository.findById(currentUserId);
        model.addAttribute("member", memberOpt.orElse(null));
        // 구독 수 (내가 구독한 사람 수)
        int subscribeCount = memberSubRepository.countBySubUserAndState(CURRENT_USER_ID, 1);
        // 구독자 수 (나를 구독한 사람 수)
        int subscribedCount = memberSubRepository.countBySubedUserAndState(CURRENT_USER_ID, 1);

        model.addAttribute("subscribeCount", subscribeCount);
        model.addAttribute("subscribedCount", subscribedCount);
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

        model.addAttribute("currentUserId", currentUserId);
        Optional<Member> memberOpt = memberRepository.findById(currentUserId);
        model.addAttribute("member", memberOpt.orElse(null));
        // 구독 수 (내가 구독한 사람 수)
        int subscribeCount = memberSubRepository.countBySubUserAndState(CURRENT_USER_ID, 1);
        // 구독자 수 (나를 구독한 사람 수)
        int subscribedCount = memberSubRepository.countBySubedUserAndState(CURRENT_USER_ID, 1);

        model.addAttribute("subscribeCount", subscribeCount);
        model.addAttribute("subscribedCount", subscribedCount);
        model.addAttribute("recipes", recipePage.getContent());
        model.addAttribute("recipePage", recipePage);
        model.addAttribute("currentUserId", currentUserId);

        return "personalized_recipes";
    }

    @GetMapping("/subscribed-users")
    @ResponseBody
    public List<Map<String, String>> getSubscribedUsers(HttpSession session) {
        String userId = (String) session.getAttribute("CURRENT_USER_ID");
        List<MemberSub> subs = memberSubRepository.findBySubUserAndState(userId, 1);

        List<Map<String, String>> result = new ArrayList<>();
        for (MemberSub sub : subs) {
            Member m = memberRepository.findById(sub.getSubedUser()).orElse(null);
            if (m != null) {
                Map<String, String> map = new HashMap<>();
                map.put("userId", m.getUserId());
                map.put("name", m.getName());
                map.put("profile", m.getProfile());
                result.add(map);
            }
        }

        return result;
    }
    
    @GetMapping("/toggle-subscribe")
    @ResponseBody
    public String toggleSubscribe(@RequestParam String subUser, @RequestParam String subedUser) {
        MemberSub sub = memberSubRepository.findBySubUserAndSubedUser(subUser, subedUser);

        if (sub != null) {
            int newState = (sub.getState() == 1) ? 0 : 1;
            sub.setState(newState);
            memberSubRepository.save(sub);
            return (newState == 1) ? "구독취소" : "구독";
        } else {
            // 처음 구독하는 경우: 새로운 구독 생성
            MemberSub newSub = new MemberSub();
            newSub.setSubUser(subUser);
            newSub.setSubedUser(subedUser);
            newSub.setState(1); // 구독 상태로 생성
            memberSubRepository.save(newSub);
            return "구독취소"; // 방금 구독했으므로 다음 누름은 '취소'
        }
    }

    @GetMapping("/edit_profile")
    public String showEditProfileForm(Model model) {
        // 현재 로그인된 사용자 ID는 상수 또는 세션에서 얻는다고 가정
        String userId = CURRENT_USER_ID;  // 혹은 session.getAttribute("userId")

        Member member = memberRepository.findById(userId).orElse(null);

        if (member == null) {
            // 회원 정보 없으면 빈 객체라도 넘기거나 에러 처리
            member = new Member();
        }

        model.addAttribute("member", member);
        return "edit_profile";
    }



    @PostMapping("/editProfileSubmit")
    public String updateProfile(@ModelAttribute Member member,
                                @RequestParam("profileImage") MultipartFile profileImage) throws IOException {
        String userId = CURRENT_USER_ID;

        Member existingMember = memberRepository.findById(userId).orElse(null);
        if (existingMember == null) {
            return "redirect:/edit_profile";
        }

        if (profileImage != null && !profileImage.isEmpty()) {
            String originalFilename = profileImage.getOriginalFilename();

            // 저장 폴더 절대 경로 (환경에 맞게 변경 가능)
            String uploadDir = new File("src/main/resources/static/images").getAbsolutePath() + File.separator;
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) uploadPath.mkdirs();

            // 원본 파일명 그대로 저장 (덮어쓰기 주의)
            profileImage.transferTo(new File(uploadDir + originalFilename));

            // DB에는 파일명만 저장
            existingMember.setProfile(originalFilename);
        }

        // 나머지 필드 업데이트
        existingMember.setName(member.getName());
        existingMember.setEmail(member.getEmail());
        existingMember.setIntro(member.getIntro());
        existingMember.setAllergy(member.getAllergy());

        memberRepository.save(existingMember);

        return "redirect:/my_recipes";
    }



}