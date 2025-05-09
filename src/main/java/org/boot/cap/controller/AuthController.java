package org.boot.cap.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.boot.cap.dto.*;
import org.boot.cap.dto.login.*;
import org.boot.cap.entity.User;
import org.boot.cap.repository.UserRepository;
import org.boot.cap.security.JwtUtil;
import org.boot.cap.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserRepository userRepository;

    // 로그인 요청 처리
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        RegisterResponse loginResult = userService.login(
                loginRequest.getUserId(),
                loginRequest.getUserPassword()
        );

        if (loginResult.isSuccess()) {
            return ResponseEntity.ok(
                    new LoginResponse(true, loginResult.getMessage(), loginResult.getAccessToken(), loginResult.getRefreshToken())
            );
        }

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

    // AccessToken, RefreshToken 재발급
    @PostMapping("/reissue-token")
    public ResponseEntity<RefreshResponse> reissueToken(@RequestHeader("Authorization") String refreshToken) {
        log.info("AuthController.reissueToken(): " + refreshToken);

        if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RefreshResponse(false, "Invalid token.", null, null));
        }

        String rawRefreshToken = refreshToken.substring(7);
        log.info(rawRefreshToken);
        String userId = jwtUtil.extractUserId(refreshToken);
        log.info("Extracted User ID: " + userId);

        // DB 에서 저장된 RefreshToken 가져오기
        Optional<String> storedRefreshToken = userService.getRefreshTokenByUserId(userId);
        if (storedRefreshToken.isEmpty() || !storedRefreshToken.get().equals(rawRefreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RefreshResponse(false, "RefreshToken does not match.", null, null));
        }

        // RefreshToken 유효성 검사
        if (jwtUtil.validateToken(refreshToken)) {
            String newAccessToken = jwtUtil.generateAccessToken(userId);
            return ResponseEntity.status(HttpStatus.OK).body(new RefreshResponse(true, "AccessToken has been reissued.", newAccessToken, refreshToken));
        } else {
            userService.updateRefreshToken(userId, null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new RefreshResponse(false, "Token expired. Please login again.", null, null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String accessToken) {
        if (!jwtUtil.validateToken(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
        }

        // accessToken 에서 userId를 추출
        String userId = jwtUtil.extractUserId(accessToken);

        // DB 에서 userId로 refreshToken 조회
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        String refreshToken = userOptional.get().getRefreshToken();

        // 두 토큰을 블랙리스트에 추가
        jwtUtil.blacklistToken(accessToken, refreshToken);

        return ResponseEntity.ok("Logged out successfully.");
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String accessToken) {
        if (!jwtUtil.validateToken(accessToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
        }

        boolean isDeleted = userService.deleteUser(accessToken);

        if (isDeleted) {
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

}
