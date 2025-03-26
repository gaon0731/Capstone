package org.boot.repository;

import org.boot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // userId로 회원 조회
    User findByUserId(String userId);

}
