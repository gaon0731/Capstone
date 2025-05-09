package org.boot.cap.service;

import lombok.RequiredArgsConstructor;
import org.boot.cap.dto.UserDTO;
import org.boot.cap.dto.login.RegisterResponse;
import org.boot.cap.entity.BlacklistedToken;
import org.boot.cap.entity.User;
import org.boot.cap.repository.TokenRepository;
import org.boot.cap.repository.UserRepository;
import org.boot.cap.security.JwtDTO;
import org.boot.cap.security.JwtUtil;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 아이디 중복 확인 기능
    public boolean isUserIdExists(String userId) {
        return userRepository.existsByUserId(userId);
    }

    // 회원가입 기능
    public RegisterResponse register(UserDTO userDTO) {
        // 아이디 중복 확인
        if (userRepository.existsByUserId(userDTO.getUserId())) {
            return new RegisterResponse(false, "userId already exists.", null, null);
        }

        // dto -> entity 로 변환
        User user = new User();
        user.setUserId(userDTO.getUserId());
        user.setUserPassword(passwordEncoder.encode(userDTO.getUserPassword())); // 비밀번호 암호화
        user.setUserName(userDTO.getUserName());

        userRepository.save(user);

        // jwt 토큰 생성
        JwtDTO jwtDTO = jwtUtil.generateToken(userDTO.getUserId());
        String accessToken = jwtDTO.getAccessToken();
        String refreshToken = jwtDTO.getRefreshToken();

        return new RegisterResponse(true, "User created.", accessToken, refreshToken);
    }

//    // 로그인 기능
//    public RegisterResponse login(String userId, String userPassword) {
//        Optional<User> userOptional = userRepository.findByUserId(userId);
//
//        // 사용자 없음
//        if (userOptional.isEmpty()) {
//            return new RegisterResponse(false, "User not found", null, null);
//        }
//
//        User user = userOptional.get();
//
//        // 비밀번호 불일치
//        if (!passwordEncoder.matches(userPassword, user.getUserPassword())) {
//            return new RegisterResponse(false, "Password mismatch", null, null);
//        }
//
//        // 기존 refreshToken 존재 여부 확인
//        Optional<String> existingRefreshToken = getRefreshTokenByUserId(userId);
//        String refreshToken;
//
//        if (existingRefreshToken.isPresent()) {
//            refreshToken = existingRefreshToken.get(); // 기존 토큰 재사용
//        } else {
//            refreshToken = jwtUtil.generateToken(userId).getRefreshToken(); // 새로 발급
//            updateRefreshToken(userId, refreshToken); // DB 저장
//        }
//
//        // AccessToken 새로 발급
//        String accessToken = jwtUtil.generateAccessToken(userId);
//
//        return new RegisterResponse(true, "Login success", accessToken, refreshToken);
//    }

    public RegisterResponse login(String userId, String userPassword) {
        Optional<User> userOptional = userRepository.findByUserId(userId);

        // 사용자 없음
        if (userOptional.isEmpty()) {
            return new RegisterResponse(false, "User not found", null, null);
        }

        User user = userOptional.get();

        // 비밀번호 불일치
        if (!passwordEncoder.matches(userPassword, user.getUserPassword())) {
            return new RegisterResponse(false, "Password mismatch", null, null);
        }

        // 기존 refreshToken 존재 여부 확인
        Optional<String> existingRefreshToken = getRefreshTokenByUserId(userId);
        String refreshToken;

        if (existingRefreshToken.isPresent()) {
            String existingToken = existingRefreshToken.get();

            if (existingToken.startsWith("Bearer ")) {
                existingToken = existingToken.substring(7);
            }
            System.out.println("검증할 토큰: " + existingToken);

            // 블랙리스트에 있는지 확인 (findByTokenValue 사용)
            Optional<BlacklistedToken> blacklistedToken = tokenRepository.findByTokenValue(existingToken);
            boolean isBlacklisted = blacklistedToken.isPresent();  // findByTokenValue로 검증

            System.out.println("블랙리스트 여부: " + isBlacklisted);

            if (isBlacklisted) {
                // 블랙리스트에 있다면 새로 발급
                refreshToken = jwtUtil.generateToken(userId).getRefreshToken();
                updateRefreshToken(userId, refreshToken); // DB에 갱신
            } else {
                // 유효한 경우 기존 토큰 재사용
                refreshToken = existingToken;
            }

        } else {
            // refresh token이 DB에 없으면 새로 발급
            refreshToken = jwtUtil.generateToken(userId).getRefreshToken();
            updateRefreshToken(userId, refreshToken);
        }

        // AccessToken은 항상 새로 발급
        String accessToken = jwtUtil.generateAccessToken(userId);

        return new RegisterResponse(true, "Login success", accessToken, refreshToken);
    }

    // 로그인 && 회원가입 시 RefreshToken 저장
    public void updateRefreshToken(String userId, String refreshToken) {
        // 'Bearer '를 제거한 토큰을 저장
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        // 사용자 ID가 존재하는지 확인 후 refreshToken 업데이트
        if (!userRepository.existsByUserId(userId)) {
            throw new RuntimeException("userId not found when updating refresh token");
        }

        // DB에 새로운 refreshToken 을 업데이트
        userRepository.updateRefreshTokenByUserId(userId, refreshToken);
    }

    // 사용자 ID 로 RefreshToken 가져오기
    public Optional<String> getRefreshTokenByUserId(String userId) {
        return userRepository.findRefreshTokenByUserId(userId);
    }

    // 회원 탈퇴 기능
    public boolean deleteUser(String accessToken) {
        // "Bearer " 제거
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        String userId = jwtUtil.extractUserId(accessToken);

        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isEmpty()) {
            return false;
        }

        String refreshToken = userOptional.get().getRefreshToken();

        // 블랙리스트 등록
        jwtUtil.blacklistToken(accessToken, refreshToken);

        userRepository.delete(userOptional.get());
        return true;
    }

    //userId로 usersId 찾기
    public Long getUsersIdFromUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("해당 유저가 존재하지 않음"));

        return user.getUsersId(); // users_id 값 리턴
    }

}
