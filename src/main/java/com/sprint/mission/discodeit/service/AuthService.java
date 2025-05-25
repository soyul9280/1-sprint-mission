package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.RoleUpdateReqeust;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.security.CustomUserDetails;

public interface AuthService {
    UserDto getMe(CustomUserDetails userDetails);
    UserDto updateRole(RoleUpdateReqeust roleUpdateReqeust);
}
