package org.boot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// 플랜 추가 후 응답을 반환할 때 사용
public class AddPlanResponse {

    private boolean success;
    private String message;

}
