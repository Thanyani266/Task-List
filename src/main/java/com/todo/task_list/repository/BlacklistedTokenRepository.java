package com.todo.task_list.repository;

import com.todo.task_list.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    Optional<BlacklistedToken> findByToken(String token);
    boolean existsByToken(String token);
    void deleteAllByBlacklistedAtBefore(java.time.LocalDateTime cutoff);
}

