package org.boot.cap.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users_cssd") // users_cssd 테이블이랑 연결
public class UsersCssd {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_cssd_id")
    private Long usersCssdId;

    // Users 테이블의 외래키
    @Column(name = "users_id", nullable = false)
    private Long usersId;

    // CSSD 테이블의 외래키
    @Column(name = "cssd_id", nullable = false)
    private Long cssdId;

    // 학습일
    @Column(name = "study_date", nullable = false)
    private LocalDate studyDate;

    // 학습 여부
    @Column(name = "study_status", nullable = false)
    private boolean studyStatus;

}
