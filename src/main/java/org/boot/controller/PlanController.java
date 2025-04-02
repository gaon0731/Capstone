package org.boot.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.boot.dto.AddPlanRequest;
import org.boot.dto.AddPlanResponse;
import org.boot.dto.PlanDTO;
import org.boot.security.JwtUtil;
import org.boot.service.PlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final JwtUtil jwtUtil;

    @PostMapping("/add-plan")
    public ResponseEntity<AddPlanResponse> addPlan(
            @RequestBody AddPlanRequest request, HttpServletRequest httpRequest) {

        String token = httpRequest.getHeader("Authorization");

        if(!jwtUtil.validateToken(token)) {
            return ResponseEntity.badRequest().body(new AddPlanResponse(false, "Invalid token."));
        }

        // jwt 에서 userId 문자열 가져오기
        String userId = jwtUtil.extractUserId(token);

        // AddPlanRequest -> PlanDTO 로 변환
        PlanDTO planDTO = new PlanDTO();
        planDTO.setStudyField(request.getStudyField());
        planDTO.setStartDate(request.getStartDate());
        planDTO.setEndDate(request.getEndDate());
        planDTO.setStudyDays(request.getStudyDays());
        planDTO.setMajorNonMajor(request.getMajorNonMajor());

        AddPlanResponse response = planService.addPlan(userId, planDTO);
        return ResponseEntity.ok(response); // 200
    }

}