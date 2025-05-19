package com.example.automaticbookingbot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class MainController {

    @GetMapping("/")
    public String rootPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", principal.getName());
        return "main";
    }

    @GetMapping("/main")
    public String mainPageDirect(Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        return "redirect:/";
    }
}
