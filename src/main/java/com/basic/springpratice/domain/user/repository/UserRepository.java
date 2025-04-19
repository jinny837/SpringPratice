package com.basic.springpratice.domain.user.repository;

import com.basic.springpratice.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 사용자명을 기준으로 User 엔티티를 조회합니다.
     * @param username 사용자명
     * @return User 엔티티 (없으면 Optional.empty())
     */
    Optional<User> findByUsername(String username);
}
