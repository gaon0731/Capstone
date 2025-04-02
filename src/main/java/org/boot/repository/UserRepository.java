package org.boot.repository;

import org.boot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // userId로 회원 조회
    Optional<User> findByUserId(String userId);

    // userId 중복 검사
    boolean existsByUserId(String userId);

    // userId로 RefreshToken 가져오기
    @Query("SELECT u.refreshToken FROM User u WHERE u.userId = :userId")
    Optional<String> findRefreshTokenByUserId(String userId);

    // userId로 RefreshToken 업데이트
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.refreshToken = :refreshToken WHERE u.userId = :userId")
    void updateRefreshTokenByUserId(String userId, String refreshToken);

}
