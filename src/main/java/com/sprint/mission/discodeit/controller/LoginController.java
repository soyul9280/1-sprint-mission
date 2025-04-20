package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.docs.LoginApi;
import com.sprint.mission.discodeit.dto.request.LoginRequestDto;
import com.sprint.mission.discodeit.dto.response.UserDto;

import com.sprint.mission.discodeit.service.basic.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LoginController implements LoginApi {
    private final LoginService loginService;

    @PostMapping("/login")
    @Override
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        log.info("로그인 요청: username = {}", loginRequestDto.getUsername());
        UserDto user = loginService.login(loginRequestDto.getUsername(), loginRequestDto.getPassword());
        log.debug("로그인 응답: {}", user);
        return ResponseEntity
                .status(HttpStatus.OK).body(user);
    }
}
