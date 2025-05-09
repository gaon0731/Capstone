package org.boot.cap.entity.study;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "eip") // eip 테이블이랑 연결
public class Eip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eip_id")
    private Long eipId;

    // 소단원
    @Column(name = "unit_num", nullable = false)
    private String unitNum;

    // 소단원 이름
    @Column(name = "unit_name", nullable = false)
    private String unitName;

    // 소단원별 학습 시간
    @Column(name = "study_time", nullable = false)
    private Integer studyTime;

    // 소단원별 중요도
    @Column(name = "unit_importance", nullable = false)
    private Integer unitImportance;

    // 소단원별 요약내용
    @Column(name = "section_summary", nullable = false)
    private String sectionSummary;

}
