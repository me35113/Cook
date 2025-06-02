package com.dita.controller;

import com.dita.domain.Member;
import com.dita.domain.SignupRequestDto;
import com.dita.service.KakaoLogoutService;
import com.dita.service.MemberService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class SignupController {

    private final MemberService memberService;
    private final KakaoLogoutService kakaoLogoutService;

    public SignupController(MemberService memberService, KakaoLogoutService kakaoLogoutService) {
        this.memberService = memberService;
        this.kakaoLogoutService = kakaoLogoutService;
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
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String userId,
                        @RequestParam String pwd,
                        Model model) {

        if (!memberService.existsByUserId(userId)) {
            model.addAttribute("loginError", "존재하지 않는 아이디입니다.");
            return "login";
        }

        Member member = memberService.findByUserId(userId);
        if (!member.getPwd().equals(pwd)) {
            model.addAttribute("loginError", "비밀번호가 일치하지 않습니다.");
            return "login";
        }

        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
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

            // 카카오 로그아웃 URL로 리디렉션 (브라우저 쿠키 제거 포함)
            String clientId = "f5a16163d9b52396f24b47c14f208fd9";
            String redirectUri = "http://113.198.238.96/login/oauth2/code/naver"; // 로그아웃 후 돌아올 페이지
            return "redirect:https://kauth.kakao.com/oauth/logout?client_id=" + clientId + "&logout_redirect_uri=" + redirectUri;
        }
       

        session.invalidate();
        return "redirect:/login";
    }

    

}
