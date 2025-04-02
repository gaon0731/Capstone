package org.boot.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users_plan") // users 테이블이랑 연결
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_plan_id")
    private Long usersPlanId;

    // users 테이블의 users_id 참조
    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private User usersId;

    // 학습 분야
    @Column(name = "study_field", nullable = false)
    private String studyField;

    // 학습 시작일
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // 학습 종료일
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // 학습 요일 목록
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "study_days", nullable = false)
    private List<String> studyDays;

    // 전공 여부
    @Column(name = "major_non_major", nullable = false)
    private String majorNonMajor;


}
