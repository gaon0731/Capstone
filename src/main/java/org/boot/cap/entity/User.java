package org.boot.cap.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users") // users 테이블이랑 연결
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long usersId;

    // 사용자 아이디
    @Column(name = "user_id", unique = true, nullable = false, length = 10)
    private String userId;

    // 사용자 비밀번호
    @Column(name = "user_password", nullable = false, length = 255)
    private String userPassword;

    // 사용자 닉네임
    @Column(name = "user_name", nullable = false, length = 20)
    private String userName;

    // 재발급 토큰
    @Column(name="refresh_token")
    private String refreshToken;

}
