package org.boot.controller;

import lombok.RequiredArgsConstructor;
import org.boot.dto.*;
import org.boot.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

//@RestController
@Controller
//@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/users/signup")
    public String showSignUpPage() {
        return "signupPage";
    }

    @GetMapping("/api/users/check-id")
    public ResponseEntity<CheckIdResponse> checkUserId(@RequestParam String userId) {
        boolean exists = userService.isUserIdExists(userId);
        CheckIdResponse response = new CheckIdResponse(!exists, exists ? "userId already exists." : "You can use this userId.");
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409
        } else {
            return ResponseEntity.ok(response); // 200
        }
    }

    @PostMapping("/api/users/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest) {
        // pw & pw 확인 일치/불일치 체크
        if (!registerRequest.getUserPassword().equals(registerRequest.getUserPasswordConfirm())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RegisterResponse(false, "Passwords do not match.")); // 400
        }

        // id 중복 체크
        if (userService.isUserIdExists(registerRequest.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new RegisterResponse(false, "userId already exists.")); // 409
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
