package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.RoleUpdateReqeust;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.CustomUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final SessionRegistry sessionRegistry;


    @Override
    public UserDto getMe(CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public UserDto updateRole(RoleUpdateReqeust roleUpdateReqeust) {
        UUID userId = roleUpdateReqeust.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.updateRole(roleUpdateReqeust.getNewRole());
        expireUserSeesions(user);
        return userMapper.toDto(user);
    }

    private void expireUserSeesions(User user) {
        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        for (Object principal : allPrincipals) {
            if(principal instanceof CustomUserDetails customUserDetails) {
                if(customUserDetails.getUser().equals(user)){
                    List<SessionInformation> session = sessionRegistry.getAllSessions(principal, false);
                    for (SessionInformation sessionInformation : session) {
                        sessionInformation.expireNow();
                    }
                }
            }
        }
    }
}
