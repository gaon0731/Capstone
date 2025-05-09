package org.boot.cap.repository;

import org.boot.cap.entity.UsersEip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsersEipRepository extends JpaRepository<UsersEip, Long> {

    @Query("SELECT COUNT(u) FROM UsersToeic u WHERE u.usersId = :usersId AND u.studyStatus = true")
    Long countCompletedEipByUsersId(@Param("usersId") Long usersId);

    List<UsersEip> findByUsersId(Long usersId);

    UsersEip findByUsersIdAndEipId(Long usersId, Long eipId);

}
