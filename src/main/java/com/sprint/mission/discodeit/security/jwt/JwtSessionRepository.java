package com.sprint.mission.discodeit.security.jwt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JwtSessionRepository extends JpaRepository<JwtSession, String> {
    Optional<JwtSession> findByRefreshToken(String refreshToken);
    void deleteByUserId(UUID userId);
    boolean existsByAccessToken(String accessToken);
    List<JwtSession> findByUserIdAndRevokedFalse(UUID userId);
}
