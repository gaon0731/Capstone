package org.boot.controller;


import org.boot.dto.*;
import org.boot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/api/users/signup")
    public String showSignUpPage() {
        return "signupPage";
    }
    @GetMapping("/api/users/check-id")
    public ResponseEntity<CheckIdResponse> checkUserId(@RequestParam String userId) {
        boolean exists = userService.isUserIdExists(userId);
        CheckIdResponse response = new CheckIdResponse(!exists, exists ? "userId already exists." : "You can use this userId");

        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } else {
            return ResponseEntity.ok(response);
        }
    }
    @PostMapping("/api/users/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        // pw & pw 재확인 비교
        if (!registerRequest.getUserPassword().equals(registerRequest.getUserPasswordConfirm())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RegisterResponse(false, "Passwords do not match."));
        }

        // UserDTO 객체 생성
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(registerRequest.getUserId());
        userDTO.setUserPassword(registerRequest.getUserPassword());
        userDTO.setUserName(registerRequest.getUserName());

        // id 중복 체크
        if (userService.isUserIdExists(userDTO.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new RegisterResponse(false, "ID already exists."));
        }

        RegisterResponse response = userService.register(userDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public String redirectToLogin() {
        return "loginPage";
    }
    @GetMapping("/api/users/login")
    public String showLoginPage() {
        return "loginPage";
    }
    @PostMapping("/api/users/login") // session : 로그인 유지
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        String userId = loginRequest.getUserId();
        String userPassword = loginRequest.getUserPassword();

        boolean isLoginSuccess = userService.login(userId, userPassword);
        // 로그인 성공 시
        if (isLoginSuccess) {
            LoginResponse response = new LoginResponse(true, "Login success.");
            return ResponseEntity.ok(response); // 200
        }
        // 로그인 실패 시
        LoginResponse response = new LoginResponse(false, "Invalid ID or PW");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // 401
    }

}
