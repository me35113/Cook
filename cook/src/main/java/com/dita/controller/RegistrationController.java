// HomeController.java
package com.dita.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RegistrationController {

    @GetMapping("/registration")
    public String index() {
        return "registration"; // templates/registration.html 을 찾아 렌더링
    }    
}

   