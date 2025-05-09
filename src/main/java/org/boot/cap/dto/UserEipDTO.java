package org.boot.cap.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEipDTO {

    private Integer usersId;
    private Integer eipId;
    private LocalDate studyDate;
    private Boolean studyStatus;
    private String unitNum;
    private String unitName;
    private Integer unitImportance;
    private String sectionSummary;

}
