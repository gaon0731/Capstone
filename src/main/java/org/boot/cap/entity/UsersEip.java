package org.boot.cap.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users_eip") // users_eip 테이블이랑 연결
public class UsersEip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_eip_id")
    private Long usersEipId;

    // Users 테이블의 외래키
    @Column(name = "users_id", nullable = false)
    private Long usersId;

    // EIP 테이블의 외래키
    @Column(name = "eip_id", nullable = false)
    private Long eipId;

    // 학습일
    @Column(name = "study_date", nullable = false)
    private LocalDate studyDate;

    // 학습 여부
    @Column(name = "study_status", nullable = false)
    private boolean studyStatus;

}
