package org.boot.cap.dto.login;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private boolean success;
    private String message;
    private String accessToken;
    private String refreshToken;

}
