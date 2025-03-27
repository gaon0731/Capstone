package org.boot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String userId;
    private String userPassword;
    private String userPasswordConfirm; // 비밀번호 재확인
    private String userName;

}
