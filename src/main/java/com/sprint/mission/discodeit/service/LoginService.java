package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    public User login(String loginId, String password) {
        Optional<User> findUserOptional = userRepository.findOptionalUserByLoginId(loginId);
        User user = findUserOptional.filter(u -> u.getPassword().equals(password))
                .orElseThrow(() -> new IllegalArgumentException("잘못된 비밀번호 입니다."));
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElse(new UserStatus());

        return user;
    }
}
