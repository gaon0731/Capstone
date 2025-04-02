package org.boot.service;

import lombok.RequiredArgsConstructor;
import org.boot.dto.AddPlanResponse;
import org.boot.dto.PlanDTO;
import org.boot.entity.Plan;
import org.boot.entity.User;
import org.boot.repository.PlanRepository;
import org.boot.repository.UserRepository;
import org.boot.security.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    @Transactional
    public AddPlanResponse addPlan(String userId, PlanDTO planDTO) {
        User user = userRepository.findByUserId(userId) // DB 에서 User 엔티티 조회
                .orElseThrow(() -> new IllegalArgumentException("User Not Found."));

        // dto -> entity 로 변환
        Plan plan = new Plan();
        plan.setUsersId(user);
        plan.setStudyField(planDTO.getStudyField());
        plan.setStartDate(planDTO.getStartDate());
        plan.setEndDate(planDTO.getEndDate());
        plan.setStudyDays(planDTO.getStudyDays());
        plan.setMajorNonMajor(planDTO.getMajorNonMajor());

        planRepository.save(plan);

        return new AddPlanResponse(true, "Plan added successfully.");
    }

}
