package com.todo.task_list.service;

import com.todo.task_list.entity.BlacklistedToken;
import com.todo.task_list.repository.BlacklistedTokenRepository;
import com.todo.task_list.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final JwtUtil jwtUtil;

    public void blacklistToken(String token) {
        Date expiry = jwtUtil.extractExpiration(token);
        BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                .token(token)
                .expiryAt(expiry.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();
        blacklistedTokenRepository.save(blacklistedToken);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }
}
