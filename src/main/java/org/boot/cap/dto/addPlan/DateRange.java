package org.boot.cap.dto.addPlan;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DateRange {

    private String title; // 설명
    private LocalDate startDate; // 시작일
    private LocalDate endDate;   // 종료일

}
