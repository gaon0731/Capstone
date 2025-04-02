package org.boot.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long usersId;
    private String userId;
    private String userPassword;
    private String userName;
    private String refreshToken;

}
