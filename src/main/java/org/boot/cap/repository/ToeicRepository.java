package org.boot.cap.repository;

import org.boot.cap.entity.study.Toeic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToeicRepository extends JpaRepository<Toeic, Long> {

    @Query("SELECT SUM(t.studyTime) FROM Toeic t")
    Integer getTotalStudyTime();  // Toeic 총 공부 시간 합산

    List<Toeic> findAllByOrderByToeicIdAsc();

    @Query("SELECT t FROM Toeic t WHERE t.unitNum = :unitNum")
    Toeic findByUnitNum(@Param("unitNum") String unitNum);

}
