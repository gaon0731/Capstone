package org.boot.entity;

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

    @Column(name = "user_id", unique = true, nullable = false, length = 10)
    private String userId;

    @Column(name = "user_password", nullable = false, length = 20)
    private String userPassword;

    @Column(name = "user_name", nullable = false, length = 20)
    private String userName;

}
