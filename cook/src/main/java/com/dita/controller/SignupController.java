package com.dita.controller;

import com.dita.domain.Member;
import com.dita.domain.SignupRequestDto;
import com.dita.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class SignupController {

    private final MemberService memberService;

    public SignupController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원가입 폼 보여주기
    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("signupRequestDto", new SignupRequestDto());
        return "signup"; // signup.html 템플릿
    }

    // 회원가입 폼 제출 처리
    @PostMapping("/signup")
    public String processSignup(@Valid @ModelAttribute SignupRequestDto signupRequestDto,
                                BindingResult result,
                                Model model) {
        // 유효성 검사 실패 시 다시 회원가입 폼
        if (result.hasErrors()) {
            return "signup";
        }

        // 아이디 중복 체크
        if (memberService.existsByUserId(signupRequestDto.getUserId())) {
            model.addAttribute("signupError", "이미 존재하는 아이디입니다.");
            return "signup";
        }

        // 회원 저장
        memberService.save(signupRequestDto);

        // 가입 후 로그인 페이지로 이동
        return "redirect:/login";
    }

    // 로그인 폼 보여주기
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // login.html
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String userId,
                        @RequestParam String pwd,
                        Model model) {

        // 여기에 로그인 로직 작성
        // 예: 사용자 존재 여부, 비밀번호 일치 여부 등
        if (!memberService.existsByUserId(userId)) {
            model.addAttribute("loginError", "아이디가 존재하지 않습니다.");
            return "login";
        }

        Member member = memberService.findByUserId(userId);
        if (!member.getPwd().equals(pwd)) { // 비밀번호 비교 (암호화 안 했을 경우)
            model.addAttribute("loginError", "비밀번호가 일치하지 않습니다.");
            return "login";
        }

        // 로그인 성공 후 리다이렉트 (예: 홈 또는 마이페이지)
        return "redirect:/home";
    }
    
    @GetMapping("/home")
    public String home() {
        return "home"; // => templates/home.html 을 렌더링
    }
    

}
