package org.boot.controller;

import lombok.RequiredArgsConstructor;
import org.boot.dto.*;
import org.boot.security.JwtDTO;
import org.boot.security.JwtUtil;
import org.boot.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    // 로그인 요청 처리
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        String userId = loginRequest.getUserId();
        String userPassword = loginRequest.getUserPassword();

        // 사용자 인증을 위한 로그인 처리
        boolean isLoginSuccess = userService.login(userId, userPassword);

        if (isLoginSuccess) {
            // JwtDTO 객체에서 accessToken 추출
            JwtDTO jwtDTO = jwtUtil.generateToken(userId);
            String accessToken = jwtDTO.getAccessToken();  // JwtDTO 에서 accessToken 을 추출
            String refreshToken = jwtDTO.getRefreshToken();

            userService.updateRefreshToken(userId, refreshToken);

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
                    .body(new RegisterResponse(false, "userId already exists.", null, null)); // 409
        }

        // RegisterRequest -> UserDTO 로 변환
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(registerRequest.getUserId());
        userDTO.setUserPassword(registerRequest.getUserPassword());
        userDTO.setUserName(registerRequest.getUserName());

        // 회원가입 성공 시
        RegisterResponse response = userService.register(userDTO);

        userService.updateRefreshToken(userDTO.getUserId(), response.getRefreshToken());

        return ResponseEntity.ok(response); // 200
    }

    // AccessToken && RefreshToken 재발급
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshRequest refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();
        String userId;

        try {
            // RefreshToken 에서 userId 추출
            userId = jwtUtil.parseClaims(refreshToken).getSubject();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RefreshResponse(false, "", null, null));
        }

        // DB 에서 저장된 RefreshToken 가져오기
        Optional<String> storedRefreshToken = userService.getRefreshTokenByUserId(userId);

        if (storedRefreshToken.isEmpty() || !storedRefreshToken.get().equals(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RefreshResponse(false, "", null, null));
        }

        // RefreshToken 이 유효한지 확인
        boolean isRefreshTokenValid = jwtUtil.validateToken(refreshToken);

        if (isRefreshTokenValid) {
            String newAccessToken = jwtUtil.generateAccessToken(userId);
            return ResponseEntity.status(HttpStatus.OK).body(new RefreshResponse(true, "", newAccessToken, refreshToken));
        } else {
            JwtDTO newTokens = jwtUtil.generateToken(userId);

            userService.updateRefreshToken(userId, newTokens.getRefreshToken());

            return ResponseEntity.status(HttpStatus.OK).body(new RefreshResponse(true, "", newTokens.getAccessToken(), newTokens.getRefreshToken()));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String bearerToken) {
        // Authorization 헤더에서 Bearer 토큰을 추출
        String token = bearerToken.substring(7);

        // 토큰을 블랙리스트에 추가하여 로그아웃 처리
        jwtUtil.blacklistToken(token);

        return ResponseEntity.ok("Logged out successfully.");
    }

    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        boolean isDeleted = userService.deleteUser(userId);

        if (isDeleted) {
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

}
