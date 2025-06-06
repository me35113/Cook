package com.dita.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dita.domain.Member;

import ch.qos.logback.core.model.Model;
import lombok.extern.java.Log;

@Controller
@Log
@RequestMapping
public class MyPagecontroller {

    @GetMapping("/ingredients_add")
    public String showIngredientAddPage() {
        return "ingredients_add"; // templates/ingredients_add.html
    }
    
    @GetMapping("/mypage")
    public String showMyPage() {
        return "mypage";
    }
    
    @GetMapping("/personalized_recipes")
    public String personalizedrecipes() {
    	return "personalized_recipes";
    }

}