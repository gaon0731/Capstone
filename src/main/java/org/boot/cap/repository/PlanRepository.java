package org.boot.cap.repository;

import org.boot.cap.entity.study.Cssd;
import org.boot.cap.entity.study.Eip;
import org.boot.cap.entity.Plan;
import org.boot.cap.entity.study.Toeic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    @Query("SELECT p FROM Plan p WHERE p.usersId.usersId = :usersId")
    List<Plan> findByUsersId(@Param("usersId") Long usersId);

    @Query("SELECT u.studyField FROM Plan u WHERE u.usersId = :usersId")
    List<String> findStudyFieldsByUsersId(@Param("userId") Long usersId);

    // 단원의 총 학습 시간을 구하는 쿼리들
    @Query("SELECT SUM(t.studyTime) FROM Toeic t")
    Integer getToeicTotalStudyTime();

    @Query("SELECT SUM(e.studyTime) FROM Eip e")
    Integer getEipTotalStudyTime();

    @Query("SELECT SUM(c.studyTime) FROM Cssd c")
    Integer getCssdTotalStudyTime();

    List<Toeic> findAllByOrderByToeicIdAsc();
    List<Eip> findAllByOrderByEipIdAsc();
    List<Cssd> findAllByOrderByCssdIdAsc();

    // 단원 찾기 쿼리들
    @Query("SELECT t FROM Toeic t WHERE t.unitNum = :unitNum")
    Toeic findToeicByUnitNum(@Param("unitNum") String unitNum);

    @Query("SELECT c FROM Cssd c WHERE c.unitNum = :unitNum")
    Cssd findCssdByUnitNum(@Param("unitNum") String unitNum);

    @Query("SELECT e FROM Eip e WHERE e.unitNum = :unitNum")
    Eip findEipByUnitNum(@Param("unitNum") String unitNum);

}
