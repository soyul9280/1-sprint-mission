package com.sprint.mission.discodeit.api;

import com.sprint.mission.discodeit.api.docs.LoginApiDocs;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LoginController implements LoginApiDocs {
    private final LoginService loginService;
    private final UserMapper userMapper;

    @PostMapping("/login")
    @Override
    public ResponseEntity<UserDto> login(@Valid @RequestBody LoginForm loginForm) {
        User login = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
        UserDto dto = userMapper.toDto(login);
        if(login == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
