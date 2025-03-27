package org.boot.service;

import jakarta.servlet.http.HttpSession;
import org.boot.dto.UserDTO;
import org.boot.dto.RegisterResponse;
import org.boot.entity.User;
import org.boot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpSession session;

    // id 중복 확인 기능
    public boolean isUserIdExists(String userId) {
        return userRepository.existsByUserId(userId);
    }

    // 회원가입 기능
    public RegisterResponse register(UserDTO userDTO) {
        // id 중복 확인
        if (userRepository.existsByUserId(userDTO.getUserId())) {
            return new RegisterResponse(false, "ID already exists");
        }

        // dto -> entity 로 변환
        User user = new User();
        user.setUserId(userDTO.getUserId());
        user.setUserPassword(userDTO.getUserPassword());
        user.setUserName(userDTO.getUserName());

        userRepository.save(user);

        return new RegisterResponse(true, "User created");
    }

    // 로그인 기능
    public boolean login(String userId, String userPassword) {
        Optional<User> userOptional = userRepository.findByUserId(userId);

        if (!userOptional.isPresent() || !userOptional.get().getUserPassword().equals(userPassword)) {
            return false;
        }

        User user = userOptional.get();
        session.setAttribute("currentUser", user); // 세션 저장

        return true;
    }

    // 로그아웃 기능
    public void logout() {
        session.invalidate(); // 세션 종료
    }

}
