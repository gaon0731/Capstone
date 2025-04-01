package org.boot.controller;

import lombok.RequiredArgsConstructor;
import org.boot.dto.*;
import org.boot.security.JwtDTO;
import org.boot.security.JwtUtil;
import org.boot.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        String userId = loginRequest.getUserId();
        String userPassword = loginRequest.getUserPassword();

        // 사용자 인증을 위한 로그인 처리
        boolean isLoginSuccess = userService.login(userId, userPassword);

        if (isLoginSuccess) {
            // JwtDTO 객체에서 accessToken & refreshToken 추출
            JwtDTO jwtDTO = jwtUtil.generateToken(userId);
            String accessToken = jwtDTO.getAccessToken();
            String refreshToken = jwtDTO.getRefreshToken();

            return ResponseEntity.ok(new LoginResponse(true, "Login success.", accessToken, refreshToken));
        }

        // 로그인 실패 시 Unauthorized 응답
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new LoginResponse(false, "Invalid ID or PW.", null, null));
    }


    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        // pw & pw 확인 일치/불일치 체크
        if (!registerRequest.getUserPassword().equals(registerRequest.getUserPasswordConfirm())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RegisterResponse(false, "Passwords do not match.", null, null)); // 400
        }

        // id 중복 체크
        if (userService.isUserIdExists(registerRequest.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new RegisterResponse(false, "userId already exists.", null,null)); // 409
        }

        // RegisterRequest -> UserDTO 로 변환
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(registerRequest.getUserId());
        userDTO.setUserPassword(registerRequest.getUserPassword());
        userDTO.setUserName(registerRequest.getUserName());

        // 회원가입 성공 시
        RegisterResponse response = userService.register(userDTO);
        return ResponseEntity.ok(response); // 200
    }

}
