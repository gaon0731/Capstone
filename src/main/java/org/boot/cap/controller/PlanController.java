package org.boot.cap.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.boot.cap.dto.addPlan.AddPlanRequest;
import org.boot.cap.dto.addPlan.AddPlanResponse;
import org.boot.cap.dto.addPlan.EipScheduleDTO;
import org.boot.cap.dto.addPlan.ToeicScheduleDTO;
import org.boot.cap.dto.PlanDTO;
import org.boot.cap.security.JwtUtil;
import org.boot.cap.service.ScheduleService;
import org.boot.cap.service.PlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final ScheduleService scheduleService;
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

    @GetMapping("/add-plan/toeic-exam")
    public ResponseEntity<List<ToeicScheduleDTO>> getToeicSchedule(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }

        List<ToeicScheduleDTO> schedules = scheduleService.getToeicSchedules();
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/add-plan/eip-exam")
    public ResponseEntity<List<EipScheduleDTO>> getExamSchedules(HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization");
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(401).build(); // 401 Unauthorized
        }

        List<EipScheduleDTO> schedules = scheduleService.fetchEipSchedules();
        return ResponseEntity.ok(schedules);
    }

}
