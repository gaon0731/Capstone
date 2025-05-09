package org.boot.cap.dto.addPlan;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EipScheduleDTO {

    private String round; // 시험 회차명 (예: "2025년 정기 기사 1회")
    private DateRange paperApplicationPeriod; // 필기시험 원서접수 기간
    private DateRange additionalPaperApplicationPeriod; // 필기시험 추가 접수 기간 (선택적)
    private DateRange paperExamPeriod; // 필기시험 시행 기간

}
