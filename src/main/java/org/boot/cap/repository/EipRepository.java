package org.boot.cap.repository;

import org.boot.cap.entity.study.Eip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EipRepository extends JpaRepository<Eip, Long> {

    @Query("SELECT SUM(e.studyTime) FROM Eip e")
    Integer getTotalStudyTime();  // Eip 총 공부 시간 합산

    List<Eip> findAllByOrderByEipIdAsc();

    @Query("SELECT e FROM Eip e WHERE e.unitNum = :unitNum")
    Eip findByUnitNum(@Param("unitNum") String unitNum);
}

