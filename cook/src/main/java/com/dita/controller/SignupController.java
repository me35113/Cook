package com.dita.controller;

import com.dita.domain.SignupRequestDto;
import com.dita.domain.Zipcode;
import com.dita.persistence.ZipcodeRepository;
import com.dita.service.KakaoLogoutService;
import com.dita.service.MemberService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class SignupController {

    private final MemberService memberService;
    private final KakaoLogoutService kakaoLogoutService;
    private final PasswordEncoder passwordEncoder;
    private final ZipcodeRepository zipcodeRepository;

    public SignupController(MemberService memberService,
                            KakaoLogoutService kakaoLogoutService,
                            PasswordEncoder passwordEncoder,
                            ZipcodeRepository zipcodeRepository) {
        this.memberService = memberService;
        this.kakaoLogoutService = kakaoLogoutService;
        this.passwordEncoder = passwordEncoder;
        this.zipcodeRepository = zipcodeRepository;
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("signupRequestDto", new SignupRequestDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@Valid @ModelAttribute SignupRequestDto signupRequestDto,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            return "signup";
        }

        if (memberService.existsByUserId(signupRequestDto.getUserId())) {
            model.addAttribute("signupError", "이미 존재하는 아이디입니다.");
            return "signup";
        }

        memberService.save(signupRequestDto);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        // 로그인 처리 및 실패는 Spring Security가 담당하므로 단순히 뷰 리턴만
        return "login";
    }

    // 직접 로그인 처리 메서드는 삭제 (Spring Security가 처리)

    /*@GetMapping("/main")
    public String main(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 익명 사용자 또는 인증 안 된 경우 로그인 페이지로 리다이렉트
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/login";
        }

        String userId = auth.getName();  // 로그인한 사용자 ID 가져오기
        model.addAttribute("userId", userId);

        return "main";
    }*/
    
    // 관리자 페이지 매핑 추가
    @GetMapping("/admin")
    public String adminPage(HttpSession session, Model model) {
        // 관리자 권한 체크
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            return "redirect:/login";
        }
        return "top2"; // top2.html 템플릿 반환
    }

    @GetMapping("/checkUserId")
    @ResponseBody
    public String checkUserId(@RequestParam String userId) {
        boolean exists = memberService.existsByUserId(userId);
        return exists ? "duplicate" : "available";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        String accessToken = (String) session.getAttribute("kakaoAccessToken");
        String registrationId = (String) session.getAttribute("registrationId");

        if ("kakao".equals(registrationId) && accessToken != null) {
            kakaoLogoutService.logoutKakao(accessToken);
            session.invalidate();

            String clientId = "f5a16163d9b52396f24b47c14f208fd9";
            String redirectUri = "http://113.198.238.96/login/oauth2/code/naver";
            return "redirect:https://kauth.kakao.com/oauth/logout?client_id=" + clientId + "&logout_redirect_uri=" + redirectUri;
        }

        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/searchAddress")
    @ResponseBody
    public String searchAddress(@RequestParam String zipcode) {
        System.out.println("조회 요청된 zipcode: [" + zipcode + "]");
        return zipcodeRepository.findByZipcode(zipcode.trim())
                .map(Zipcode::getAddress)
                .orElse("주소 없음");
    }

    @GetMapping("/addressSearchPopup")
    public String addressSearchPopup() {
        return "addressSearchPopup";
    }
}
