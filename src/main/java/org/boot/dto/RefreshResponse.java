package org.boot.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshResponse {

    private boolean success;
    private String message;
    private String accessToken;
    private String refreshToken;

}
