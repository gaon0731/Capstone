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

    @PostMapping("/{userId}/add-plan")
    public ResponseEntity<AddPlanResponse> addPlan(
            @PathVariable String userId, // url 에서 userId 추출
            @RequestBody AddPlanRequest request, HttpServletRequest httpRequest) {

        String token = httpRequest.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(new AddPlanResponse(false, "Invalid token."));
        }

        token = token.substring(7);

        // jwt 에서 userId 문자열 가져오기
        String tokenUserId = jwtUtil.extractUserId(token);

        // url 의 userId && jwt 의 userId 일치하는지 체크
        if (!tokenUserId.equals(userId)) {
            return ResponseEntity.status(403).body(new AddPlanResponse(false, "Unauthorized user.")); // 403
        }

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
