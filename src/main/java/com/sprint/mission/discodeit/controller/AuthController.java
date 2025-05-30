package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.request.RoleUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.JwtService;
import com.sprint.mission.discodeit.security.jwt.TokenPair;
import com.sprint.mission.discodeit.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserRepository userRepository;


  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
    try {
      Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
      User user = userRepository.findByUsername(loginRequest.username()).orElseThrow(UserNotFoundException::new);
      TokenPair tokenPair = jwtService.createTokenPair(user);
      Cookie refreshCookie = new Cookie("refresh_token", tokenPair.getRefreshToken());
      refreshCookie.setHttpOnly(true);
      refreshCookie.setSecure(true);
      refreshCookie.setPath("/");
      refreshCookie.setMaxAge(7 * 24 * 60 * 60);
      response.addCookie(refreshCookie);
      return ResponseEntity.ok(tokenPair.getAccessToken());
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @GetMapping("/csrf-token")
  public ResponseEntity<CsrfToken> getCsrfToken(CsrfToken csrfToken) {
    log.debug("CSRF 토큰 요청");
    return ResponseEntity.status(HttpStatus.OK).body(csrfToken);
  }

  @GetMapping("/me")
  public ResponseEntity<String> me(HttpServletRequest request) {
    log.info("내 정보 조회 요청");
    Cookie[] cookies = request.getCookies();
    if(cookies == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String refreshToken=null;
    for (Cookie cookie : cookies) {
      if("refresh_token".equals(cookie.getName())) {
        refreshToken = cookie.getValue();
        break;
      }
    }

    if(refreshToken == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    Optional<String> accessToken=jwtService.getAccessTokenByRefreshToken(refreshToken);
    if(accessToken.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    return ResponseEntity.ok(accessToken.get());
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletRequest request,HttpServletResponse response) {
    Cookie[] cookies = request.getCookies();
    if(cookies == null) {
      return ResponseEntity.badRequest().build();
    }
    String refreshToken=null;
    for (Cookie cookie : cookies) {
      if("refresh_token".equals(cookie.getName())) {
        refreshToken = cookie.getValue();
        break;
      }
    }
    if(refreshToken == null) {
      return ResponseEntity.badRequest().build();
    }
    jwtService.invalidRefreshToken(refreshToken);
    Cookie deleteCookie = new Cookie("refresh_token", null);
    deleteCookie.setMaxAge(0);
    deleteCookie.setPath("/");
    response.addCookie(deleteCookie);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(HttpServletRequest request,HttpServletResponse response) {
    Cookie[] cookies = request.getCookies();
    if(cookies == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    String refreshToken=null;
    for (Cookie cookie : cookies) {
      if("refresh_token".equals(cookie.getName())) {
        refreshToken = cookie.getValue();
        break;
      }
    }
    if(refreshToken == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    if(!jwtService.validateToken(refreshToken)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    TokenPair newTokenPair = jwtService.reissueTokenPair(refreshToken);
    Cookie refreshCookie = new Cookie("refresh_token", newTokenPair.getRefreshToken());
    refreshCookie.setHttpOnly(true);
    refreshCookie.setSecure(true);
    refreshCookie.setPath("/");
    refreshCookie.setMaxAge(7 * 24 * 60 * 60);
    response.addCookie(refreshCookie);
    return ResponseEntity.ok(newTokenPair.getAccessToken());
  }


  @PutMapping("/role")
  public ResponseEntity<UserDto> role(@RequestBody RoleUpdateRequest request) {
    log.info("권한 수정 요청");
    UserDto userDto = authService.updateRole(request);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDto);
  }
}
