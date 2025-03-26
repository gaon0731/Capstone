package org.boot.controller;

import org.boot.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    // 메인 페이지로 이동
    @GetMapping("/mainPage")
    public String mainPage(Model model, HttpSession session) {
        // 세션에서 로그인한 사용자 정보 가져오기
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {  // 로그인된 사용자일 경우
            model.addAttribute("userId", loggedInUser.getUserId());
            model.addAttribute("userName", loggedInUser.getUserName());
        } else {  // 로그인되지 않은 경우
            return "redirect:/user/loginPage";
        }
        return "mainPage";
    }

    // 로그아웃 기능
    @GetMapping("/user/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 삭제 (로그아웃)
        return "redirect:/user/loginPage";
    }

}
