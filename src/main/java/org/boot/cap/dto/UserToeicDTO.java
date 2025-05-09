package org.boot.cap.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserToeicDTO {

    private Integer usersId;
    private Integer toeicId;
    private LocalDate studyDate;
    private Boolean studyStatus;
    private String unitNum;
    private String unitName;
    private Integer unitImportance;
    private String sectionSummary;

}
