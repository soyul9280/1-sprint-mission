package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.auth.LoginUserNotFoundException;
import com.sprint.mission.discodeit.exception.auth.WrongPasswordException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LoginService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto login(String username, String password) {
        log.debug("로그인 시도: username={}", username);
        User user = userRepository.findUserByUsername(username).orElseThrow(() -> new LoginUserNotFoundException(username));
        if(!user.getPassword().equals(password)) {
            throw new WrongPasswordException(username);
        }
        log.info("로그인 성공: userId={},username={}", user.getId(), username);
        return userMapper.toDto(user);
    }
}
