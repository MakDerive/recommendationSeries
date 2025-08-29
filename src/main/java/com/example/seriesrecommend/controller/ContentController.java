package com.example.seriesrecommend.controller;

import com.example.seriesrecommend.entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContentController {
    @GetMapping("/")
    public String index(
            Model model
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            UserEntity userDetails = (UserEntity) auth.getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
            model.addAttribute("email", userDetails.getEmail());
        }
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

}