package com.example.automaticbookingbot.controller;

import com.example.automaticbookingbot.dto.UserLoginDto;
import com.example.automaticbookingbot.dto.UserRegisterDto;
import com.example.automaticbookingbot.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        if (!model.containsAttribute("userLoginDto")) {
            model.addAttribute("userLoginDto", new UserLoginDto());
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("userRegisterDto")) {
            model.addAttribute("userRegisterDto", new UserRegisterDto());
        }
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("userRegisterDto") UserRegisterDto userRegisterDto,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes, Model model) {

        if (userService.usernameExists(userRegisterDto.getUsername())) {
            bindingResult.rejectValue("username", "user.exists", "이미 사용 중인 사용자 이름입니다.");
        }
        if (userService.emailExists(userRegisterDto.getEmail())) {
            bindingResult.rejectValue("email", "email.exists", "이미 사용 중인 이메일입니다.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("userRegisterDto", userRegisterDto); // CSRF 토큰 재생성을 위해 모델에 다시 추가
            return "register";
        }

        userService.registerNewUser(userRegisterDto);
        redirectAttributes.addFlashAttribute("message", "회원가입이 성공적으로 완료되었습니다. 로그인해주세요.");
        return "redirect:/login?registered";
    }
}
