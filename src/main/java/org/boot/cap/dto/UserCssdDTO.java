package org.boot.cap.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCssdDTO {

    private Integer usersId;
    private Integer cssdId;
    private LocalDate studyDate;
    private Boolean studyStatus;
    private String unitNum;
    private String unitName;
    private Integer unitImportance;
    private String sectionSummary;

}
