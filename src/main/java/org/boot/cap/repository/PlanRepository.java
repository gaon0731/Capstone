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

}
