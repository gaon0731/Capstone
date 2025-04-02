package org.boot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 클라이언트가 플랜을 추가할 때 필요한 데이터만 포함함
public class AddPlanRequest {

    private String studyField;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> studyDays;
    private String majorNonMajor;

}