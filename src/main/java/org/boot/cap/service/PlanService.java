package org.boot.cap.service;

import lombok.RequiredArgsConstructor;
import org.boot.cap.dto.addPlan.AddPlanResponse;
import org.boot.cap.dto.PlanDTO;
import org.boot.cap.entity.*;
import org.boot.cap.entity.study.Cssd;
import org.boot.cap.entity.study.Eip;
import org.boot.cap.entity.study.Toeic;
import org.boot.cap.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final ToeicRepository toeicRepository;
    private final EipRepository eipRepository;
    private final CssdRepository cssdRepository;
    private final UsersToeicRepository usersToeicRepository;
    private final UsersCssdRepository usersCssdRepository;
    private final UsersEipRepository usersEipRepository;

    public List<String> getStudyFieldsByUsersId(Long usersId) {
        return planRepository.findStudyFieldsByUsersId(usersId);
    }

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

        // 주어진 날짜 범위와 학습할 요일을 기반으로 공부할 날짜 목록 생성
        List<LocalDate> studyDatesList = getStudyDatesList(planDTO.getStartDate(), planDTO.getEndDate(), planDTO.getStudyDays());
        if (studyDatesList.isEmpty()) {
            // 기본 날짜 추가해서 무조건 1개 이상 있게 만들기
            studyDatesList.add(LocalDate.now());
        }

        // 공부할 날짜 개수
        int count = studyDatesList.size();

        // 각 과목에 대한 전체 학습 시간(totalStudyTime) 계산
        String tableName = plan.getStudyField(); // "toeic", "eip", "cssd" 중 하나
        Integer totalStudyTime = getTotalStudyTime(tableName);

        // 하루 공부 시간
        Integer studyTimeByMajor = totalStudyTime/count;

        // majorNonMajor에 따라 가중치 적용
        studyTimeByMajor = (totalStudyTime != null)
                ? (int) (plan.getMajorNonMajor().equalsIgnoreCase("major") ? studyTimeByMajor * 1.4
                : plan.getMajorNonMajor().equalsIgnoreCase("others") ? studyTimeByMajor * 1.2
                : studyTimeByMajor)
                : 0; // totalStudyTime이 null이면 기본값 0

        distributeStudyTime(user.getUsersId(), plan.getStudyField(),studyDatesList, studyTimeByMajor, totalStudyTime);

        planRepository.save(plan);
        return new AddPlanResponse(true, "Plan added successfully.");
    }

    private List<LocalDate> getStudyDatesList(LocalDate startDate, LocalDate endDate, List<String> studyDays) {
        List<LocalDate> studyDatesList = new ArrayList<>();

        // 문자열 요일 리스트를 DayOfWeek로 변환 (예: ["MON", "TUE"] -> [DayOfWeek.MONDAY, DayOfWeek.TUESDAY])
        List<DayOfWeek> validDays = studyDays.stream()
                .map(day -> DayOfWeek.valueOf(day.toUpperCase()))
                .toList();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            if (validDays.contains(currentDate.getDayOfWeek())) {
                studyDatesList.add(currentDate);
            }
            currentDate = currentDate.plusDays(1);
        }

        return studyDatesList;
    }

    public Integer getTotalStudyTime(String studyField) {
        if ("toeic".equalsIgnoreCase(studyField)) {
            return toeicRepository.getTotalStudyTime();
        } else if ("eip".equalsIgnoreCase(studyField)) {
            return eipRepository.getTotalStudyTime();
        } else if ("cssd".equalsIgnoreCase(studyField)) {
            return cssdRepository.getTotalStudyTime();
        }
        return 0;
    }

    @Transactional
    public void distributeStudyTime(Long usersId, String studyField, List<LocalDate> studyDatesList, Integer studyTimeByMajor, Integer totalStudyTime) {
        // 사용자가 선택한 과목(studyField)에 따라 해당 과목의 전체 공부 리스트 불러오기
        List<?> studyList = fetchStudyList(studyField);

        // 날짜별로 하루 공부 시간 나누기
        int dayCount = studyDatesList.size();
        int[] remainingTimesPerDay = initializeDailyTimeLimit(dayCount, totalStudyTime);

        int dayIndex = 0; // 현재 날짜 인덱스를 추적하는 변수
        Long lastStudyId = null; // 마지막 소단원 id 추적용

        // 소단원별로 공부 시간 분배
        for (Object studyItem : studyList) {
            // 과목별 공부 시간과 id 설정
            int remainingStudyTime = getStudyTime(studyItem);
            Long studyId = getStudyId(studyItem);

            lastStudyId = studyId; // 마지막으로 사용한 소단원 id 저장

            // 과목 공부 시간이 다 끝날 때까지 날짜에 분배해서 저장
            while (remainingStudyTime > 0 && dayIndex < dayCount) {
                // 현재 날짜(dayCount)에 남은 공부 시간
                int availableTime = remainingTimesPerDay[dayIndex];

                if (availableTime > 0) {
                    int timeToAssign = Math.min(availableTime, remainingStudyTime);
                    LocalDate date = studyDatesList.get(dayIndex);

                    saveStudyRecord(usersId, studyField, studyId, date);

                    // 시간 차감
                    remainingStudyTime -= timeToAssign;
                    remainingTimesPerDay[dayIndex] -= timeToAssign;
                }

                // 만약 해당 날짜의 시간이 모두 소진되면 다음 날짜로 넘어감
                if (remainingTimesPerDay[dayIndex] == 0) {
                    dayIndex++;
                }
            }

            // 날짜 다 써버리면 break
            if (dayIndex >= dayCount) break;
        }

        // 남은 날짜에 복습 계획 추가 (마지막 소단원 반복)
        while (dayIndex < dayCount && lastStudyId != null) {
            saveStudyRecord(usersId, studyField, lastStudyId, studyDatesList.get(dayIndex));
            dayIndex++;
        }

    }

    private List<?> fetchStudyList(String studyField) {
        return switch (studyField.toLowerCase()) {
            case "toeic" -> toeicRepository.findAllByOrderByToeicIdAsc();
            case "eip" -> eipRepository.findAllByOrderByEipIdAsc();
            case "cssd" -> cssdRepository.findAllByOrderByCssdIdAsc();
            default -> throw new IllegalArgumentException("Invalid studyField: " + studyField);
        };
    }

    private int[] initializeDailyTimeLimit(int dayCount, int totalTime) {
        int dailyTime = totalTime / dayCount; // 각 날짜마다 할당할 수 있는 시간 = 총 공부 시간 / 날짜 수
        int[] times = new int[dayCount];
        Arrays.fill(times, dailyTime);
        return times;
    }

    private int getStudyTime(Object studyItem) {
        if (studyItem instanceof Toeic toeic) return toeic.getStudyTime();
        if (studyItem instanceof Eip eip) return eip.getStudyTime();
        if (studyItem instanceof Cssd cssd) return cssd.getStudyTime();
        throw new IllegalArgumentException("Unknown study item type");
    }

    private Long getStudyId(Object studyItem) {
        if (studyItem instanceof Toeic toeic) return toeic.getToeicId();
        if (studyItem instanceof Eip eip) return eip.getEipId();
        if (studyItem instanceof Cssd cssd) return cssd.getCssdId();
        throw new IllegalArgumentException("Unknown study item type");
    }

    private void saveStudyRecord(Long usersId, String studyField, Long studyId, LocalDate date) {
        switch (studyField.toLowerCase()) {
            case "toeic" -> {
                UsersToeic entity = new UsersToeic();
                entity.setUsersId(usersId);
                entity.setToeicId(studyId);
                entity.setStudyDate(date);
                entity.setStudyStatus(false);
                usersToeicRepository.save(entity);
            }
            case "eip" -> {
                UsersEip entity = new UsersEip();
                entity.setUsersId(usersId);
                entity.setEipId(studyId);
                entity.setStudyDate(date);
                entity.setStudyStatus(false);
                usersEipRepository.save(entity);
            }
            case "cssd" -> {
                UsersCssd entity = new UsersCssd();
                entity.setUsersId(usersId);
                entity.setCssdId(studyId);
                entity.setStudyDate(date);
                entity.setStudyStatus(false);
                usersCssdRepository.save(entity);
            }
        }
    }

}
