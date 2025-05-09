package org.boot.cap.repository;

import org.boot.cap.entity.UsersCssd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsersCssdRepository extends JpaRepository<UsersCssd, Long> {

    @Query("SELECT COUNT(u) FROM UsersToeic u WHERE u.usersId = :usersId AND u.studyStatus = true")
    Long countCompletedCssdByUsersId(@Param("usersId") Long usersId);

    List<UsersCssd> findByUsersId(Long usersId);

    UsersCssd findByUsersIdAndCssdId(Long usersId, Long cssdId);

}
