package org.boot.controller;

import lombok.RequiredArgsConstructor;
import org.boot.dto.*;
import org.boot.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/check-id")
    public ResponseEntity<CheckIdResponse> checkUserId(@RequestParam String userId) {
        boolean exists = userService.isUserIdExists(userId);
        CheckIdResponse response = new CheckIdResponse(!exists, exists ? "userId already exists." : "You can use this userId.");
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response); // 409
        } else {
            return ResponseEntity.ok(response); // 200
        }
    }

}
