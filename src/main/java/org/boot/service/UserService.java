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
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getUserPassword().equals(userPassword)) {  // 암호화 x (추후 암호화 해야 함)
                session.setAttribute("loggedInUser", user);  // 로그인 성공 시 세션에 사용자 정보 저장
                System.out.println("로그인 성공, 세션 저장됨 : " + session.getAttribute("loggedInUser"));
                return true;
            }
        }
        System.out.println("로그인 실패, 세션 저장 안 됨");
        return false;
    }

    // 로그아웃 기능
    public void logout() {
        session.invalidate(); // 세션 종료
    }

}
