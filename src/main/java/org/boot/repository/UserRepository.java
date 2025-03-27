package org.boot.repository;

import org.boot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // userId로 회원 조회
    Optional<User> findByUserId(String userId);

    // userId 중복 검사
    boolean existsByUserId(String userId);

}
