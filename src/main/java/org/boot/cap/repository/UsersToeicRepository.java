package org.boot.cap.repository;

import org.boot.cap.entity.UsersToeic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsersToeicRepository extends JpaRepository<UsersToeic, Long> {

    @Query("SELECT COUNT(u) FROM UsersToeic u WHERE u.usersId = :usersId AND u.studyStatus = true")
    Long countCompletedToeicByUsersId(@Param("usersId") Long usersId);

    List<UsersToeic> findByUsersId(Long usersId);

    UsersToeic findByUsersIdAndToeicId(Long usersId, Long toeicId);

}
