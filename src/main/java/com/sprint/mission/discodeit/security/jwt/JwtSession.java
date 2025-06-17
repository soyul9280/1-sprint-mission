package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class JwtSession extends BaseEntity {

  @Column(columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID userId;
  @Column(columnDefinition = "varchar(512)", nullable = false, unique = true)
  private String accessToken;
  @Column(columnDefinition = "varchar(512)", nullable = false, unique = true)
  private String refreshToken;
  @Column(columnDefinition = "timestamp with time zone", nullable = false)
  private Instant expiresAt;

  public JwtSession(UUID userId, String accessToken, String refreshToken, Instant expirationTime) {
    this.userId = userId;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.expiresAt = expirationTime;
  }

  public boolean isExpired() {
    return this.expiresAt.isBefore(Instant.now());
  }

  public void update(String accessToken, String refreshToken, Instant expirationTime) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
    this.expiresAt = expirationTime;
  }
}
