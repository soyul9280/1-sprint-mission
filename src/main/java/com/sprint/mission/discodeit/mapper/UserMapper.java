package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final BinaryContentMapper binaryContentMapper;
    private final SessionRegistry sessionRegistry;
    public UserDto toDto(User user) {
        if(user == null) {
            return null;
        }
        return new UserDto(
                user.getUsername(),
                user.getEmail(),
                binaryContentMapper.toDto(user.getProfile()),
                isUserOnline(user.getUsername()),
                Role.ROLE_USER
        );
    }
    private boolean isUserOnline(String username) {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(principal->principal instanceof UserDetails)
                .map(principal->(UserDetails) principal)
                .anyMatch(userDetails -> userDetails.getUsername().equals(username));
    }
}
