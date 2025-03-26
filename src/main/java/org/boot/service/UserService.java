package org.boot.service;

import org.boot.dto.UserDTO;
import org.boot.entity.User;
import org.boot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // userId로 회원 정보 조회
    public User getUserByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    // 회원가입 기능
    public String register(UserDTO userDTO) {
        // userId 중복 체크
        if (userRepository.findByUserId(userDTO.getUserId()) != null) {
            return "ID already exists";
        }

        User user = new User();
        user.setUserId(userDTO.getUserId());
        user.setUserPassword(userDTO.getUserPassword());
        user.setUserName(userDTO.getUserName());

        userRepository.save(user);
        return "User created";
    }

    // 로그인 기능


}
