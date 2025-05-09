package org.boot.cap.repository;

import org.boot.cap.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<BlacklistedToken, Long> {

    boolean existsByTokenValue(String tokenValue);

    // BlacklistedToken에서 tokenValue로 조회하는 메서드
    @Query("SELECT b FROM BlacklistedToken b WHERE b.tokenValue = :tokenValue")
    Optional<BlacklistedToken> findByTokenValue(@Param("tokenValue") String tokenValue);
}
