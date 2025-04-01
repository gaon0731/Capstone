package org.boot.controller;

import lombok.RequiredArgsConstructor;
import org.boot.dto.LoginRequest;
import org.boot.dto.LoginResponse;
import org.boot.security.JwtDTO;
import org.boot.security.JwtUtil;
import org.boot.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

//@RestController
@Controller
//@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @GetMapping("/")
    public String redirectToLogin() {
        return "loginPage";
    }

    @GetMapping("/api/users/login")
    public String showLoginPage() {
        return "loginPage";
    }

    // 로그인 요청 처리
    @PostMapping("/api/users/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        String userId = loginRequest.getUserId();
        String userPassword = loginRequest.getUserPassword();

        // 사용자 인증을 위한 로그인 처리
        boolean isLoginSuccess = userService.login(userId, userPassword);

        if (isLoginSuccess) {
            // JwtDTO 객체에서 accessToken 추출
            JwtDTO jwtDTO = jwtUtil.generateToken(userId);
            String token = jwtDTO.getAccessToken();  // JwtDTO에서 accessToken을 추출

            return ResponseEntity.ok(new LoginResponse(true, "Login success.", token));
        }

        // 로그인 실패 시 Unauthorized 응답
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new LoginResponse(false, "Invalid ID or PW.", null));
    }

}
