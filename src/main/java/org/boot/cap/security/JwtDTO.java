package org.boot.cap.security;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class JwtDTO {

    private final String grantType;
    private final String accessToken;
    private final String refreshToken;

}
