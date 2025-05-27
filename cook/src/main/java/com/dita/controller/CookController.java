package com.dita.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.dita.domain.User;
import com.dita.persistence.UserRepository;

@Controller
public class CookController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new com.dita.domain.User());
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute com.dita.domain.User user) {
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
}
