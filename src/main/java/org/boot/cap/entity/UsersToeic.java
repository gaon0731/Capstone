package org.boot.cap.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users_toeic") // users_toeic 테이블이랑 연결
public class UsersToeic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_toeic_id")
    private Long usersToeicId;

    // Users 테이블의 외래키
    @Column(name = "users_id", nullable = false)
    private Long usersId;

    // Toeic 테이블의 외래키
    @Column(name = "toeic_id", nullable = false)
    private Long toeicId;

    // 학습일
    @Column(name = "study_date", nullable = false)
    private LocalDate studyDate;

    // 학습 여부
    @Column(name = "study_status", nullable = false)
    private boolean studyStatus;

}
