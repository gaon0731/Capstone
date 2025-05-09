package org.boot.cap.dto.login;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckIdResponse {

    private boolean success;
    private String message;

}
