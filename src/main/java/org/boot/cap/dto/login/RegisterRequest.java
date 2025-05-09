package org.boot.cap.dto.login;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String userId;
    private String userPassword;
    private String userPasswordConfirm;
    private String userName;

}
