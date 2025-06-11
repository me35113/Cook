package com.dita.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.dita.domain.Member;
import com.dita.domain.Recipe;
import com.dita.persistence.MemberService;
import com.dita.persistence.RecipeService;

@Controller
public class MainController {

    private final RecipeService recipeService;
    private final MemberService memberService;

    public MainController(RecipeService recipeService, MemberService memberService) {
        this.recipeService = recipeService;
        this.memberService = memberService;
    }

    @GetMapping({"/", "/main"})
    public String mainPage(Model model) {
        // 데이터 받아오기
        List<Recipe> bestList = recipeService.getBestRecipes();
        List<Recipe> newList = recipeService.getNewRecipes();
        List<Recipe> randomList = recipeService.getRandomRecipes();
        List<Member> memberList = memberService.getAllMembers();

        // 시큐리티 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String loginId = null;

        if (authentication != null && authentication.isAuthenticated() &&
            !"anonymousUser".equals(authentication.getPrincipal())) {

            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails) {
                // 일반 로그인 유저
                loginId = ((UserDetails) principal).getUsername();

            } else if (principal instanceof OAuth2User) {
                // OAuth2 로그인 유저
                OAuth2User oauthUser = (OAuth2User) principal;

                loginId = oauthUser.getAttribute("email");
                if (loginId == null) {
                    Object idAttr = oauthUser.getAttribute("id");
                    loginId = (idAttr != null) ? idAttr.toString() : null;
                }
                // 그래도 없으면 fallback
                if (loginId == null) {
                    loginId = oauthUser.getName();  // 기본적으로 OAuth2User의 이름
                }
            }
        }

        // 모델에 데이터 담기
        model.addAttribute("bestList", bestList);
        model.addAttribute("newList", newList);
        model.addAttribute("randomList", randomList);
        model.addAttribute("memberList", memberList);
        model.addAttribute("loginId", loginId);

        return "main"; // templates/main.html 렌더링
    }
}
