package org.boot.controller;

import org.boot.dto.UserDTO;
import org.boot.entity.User;
import org.boot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 회원가입 페이지로 이동
    @GetMapping("/signup")
    public String signUpPage() {
        return "SignUpPage";
    }
    // 회원가입 처리
    @PostMapping("/register")
    public String register(UserDTO userDTO, Model model) {
        String result = userService.register(userDTO);

        if (result.equals("User created")) {
            //return "redirect:/login";
            // 성공 메시지 전달
            model.addAttribute("message", result);
            return "SignUpPage";
        } else {
            model.addAttribute("message", result);
            return "SignUpPage";
        }
    }

    @GetMapping("/profile/{userId}")
    public User getUserProfile(@PathVariable String userId) {
        return userService.getUserByUserId(userId);
    }

}
