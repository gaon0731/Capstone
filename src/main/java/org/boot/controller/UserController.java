package org.boot.controller;

import org.boot.dto.UserDTO;
import org.boot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/user/loginPage";
    }

    // 로그인 페이지로 이동
    @GetMapping("/user/loginPage")
    public String loginPage() {
        return "loginPage";
    }
    // 로그인 처리
    @PostMapping("/user/login") // session : 로그인 유지
    public String login(@RequestParam String userId, @RequestParam String userPassword) {
        boolean isLoginSuccess = userService.login(userId, userPassword);
        if (isLoginSuccess) {
            return "redirect:/mainPage";
        }
        return "redirect:/user/loginPage";
    }

    // 회원가입 페이지로 이동
    @GetMapping("/user/signupPage")
    public String signUpPage() {
        return "signupPage";
    }
    // 회원가입 처리
    @PostMapping("/user/register")
    public String register(UserDTO userDTO, Model model) {
        String result = userService.register(userDTO);

        if (result.equals("User created")) {
            return "redirect:/user/loginPage";
        } else {
            model.addAttribute("message", result);
            return "redirect:/user/signupPage";
        }
    }

}
