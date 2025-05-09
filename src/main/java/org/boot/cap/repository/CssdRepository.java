package org.boot.cap.repository;

import org.boot.cap.entity.study.Cssd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CssdRepository extends JpaRepository<Cssd, Long> {

    @Query("SELECT SUM(c.studyTime) FROM Cssd c")
    Integer getTotalStudyTime();  // Cssd 총 공부 시간 합산

    List<Cssd> findAllByOrderByCssdIdAsc();

    @Query("SELECT c FROM Cssd c WHERE c.unitNum = :unitNum")
    Cssd findByUnitNum(@Param("unitNum") String unitNum);

}
