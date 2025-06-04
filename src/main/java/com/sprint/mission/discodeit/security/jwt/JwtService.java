package com.sprint.mission.discodeit.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    private final JwtSessionRepository jwtSessionRepository;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;
    private Key key;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(UserDto userDto,long expirationMs) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiration = Date.from(now.plusMillis(expirationMs));

        return Jwts.builder()
                .claim("userDto", userDto)
                .claim("type", "access")
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(UserDto userDto,long expirationMs) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiration = Date.from(now.plusMillis(expirationMs));
        return Jwts.builder()
                .claim("userDto", userDto)
                .claim("type", "refresh")
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Transactional
    public TokenPair createTokenPair(User user){
        jwtSessionRepository.deleteByUserId(user.getId());
        UserDto dto = userMapper.toDto(user);
        Instant now = Instant.now();
        Instant expiresAt=now.plusMillis(refreshTokenExpiration);

        String accessToken = createAccessToken(dto, accessTokenExpiration);
        String refreshToken = createRefreshToken(dto, refreshTokenExpiration);

        JwtSession session=JwtSession.builder()
                .userId(user.getId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .createdAt(now)
                .expiresAt(expiresAt)
                .revoked(false)
                .build();
        jwtSessionRepository.save(session);
        return new TokenPair(accessToken, refreshToken);
    }

    @Transactional
    public TokenPair reissueTokenPair(String oldRefreshToken) {
        JwtSession oldSession=jwtSessionRepository.findByRefreshToken(oldRefreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        if (oldSession.isRevoked() || oldSession.isExpired() || !validateToken(oldRefreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }
        UserDto userDto=extractUserDto(oldRefreshToken);
        String newAccessToken = createAccessToken(userDto, accessTokenExpiration);
        String newRefreshToken = createRefreshToken(userDto, refreshTokenExpiration);

        Instant now = Instant.now();
        Instant expiresAt=now.plusMillis(refreshTokenExpiration);

        JwtSession newSession = JwtSession.builder()
                .userId(oldSession.getUserId())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .createdAt(now)
                .expiresAt(expiresAt)
                .revoked(false)
                .build();
        jwtSessionRepository.save(newSession);
        oldSession.setRevoked(true);
        oldSession.setReplacedBy(newRefreshToken);
        jwtSessionRepository.save(oldSession);
        return new TokenPair(newAccessToken, newRefreshToken);

    }

    @Transactional
    public void invalidRefreshToken(String oldRefreshToken) {
        jwtSessionRepository.findByRefreshToken(oldRefreshToken).ifPresent(session->{
            session.setRevoked(true);
            jwtSessionRepository.save(session);
        });
    }

    public UserDto extractUserDto(String oldRefreshToken) {
        try{
            Claims claims=Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(oldRefreshToken).getBody();

            String json= (String) claims.get("userDto");
            return objectMapper.readValue(json, UserDto.class);
        }catch (Exception e){
            throw new RuntimeException("토큰에서 사용자 정보 추출 실패",e);
        }
    }

    public boolean validateToken(String oldRefreshToken) {
        try{
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(oldRefreshToken);
            return claimsJws.getBody().getExpiration().after(new Date());
        }catch (JwtException e){
            log.warn("JWT 유효성 검사 실패: {}", e.getMessage());
            return false;
        }
    }

    public Optional<String> getAccessTokenByRefreshToken(String refreshToken) {
        return jwtSessionRepository.findByRefreshToken(refreshToken)
                .filter(JwtSession::isValid)
                .map(JwtSession::getAccessToken);
    }
}
