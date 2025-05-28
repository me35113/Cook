package com.dita.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.dita.domain.Member;
import com.dita.persistence.MemberRepository;

@Controller
public class CookController {

    @Autowired
    private MemberRepository memberRepository;

    // 회원가입 폼 페이지
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("member", new Member());
        return "signup"; // templates/signup.html (Thymeleaf 기준)
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String processSignup(@ModelAttribute Member member) {
    	memberRepository.save(member);
        return "redirect:/login";
    }

    // 로그인 폼 페이지
    @GetMapping("/login")
    public String loginForm() {
        return "login"; // templates/login.html
    }
}
