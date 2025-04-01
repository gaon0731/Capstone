package org.boot.service;

import lombok.RequiredArgsConstructor;
import org.boot.dto.UserDTO;
import org.boot.dto.RegisterResponse;
import org.boot.entity.User;
import org.boot.repository.UserRepository;
import org.boot.security.JwtDTO;
import org.boot.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
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

    // 로그인 기능
    public boolean login(String userId, String userPassword) {
        Optional<User> userOptional = userRepository.findByUserId(userId);

        // 사용자 없음
        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();

        // 비밀번호 불일치
        if (!passwordEncoder.matches(userPassword, user.getUserPassword())) {
            return false;
        }

        return true;
    }

}
