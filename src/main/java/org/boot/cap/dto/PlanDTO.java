package org.boot.cap.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanDTO {

    private String studyField;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> studyDays;
    private String majorNonMajor;

}
