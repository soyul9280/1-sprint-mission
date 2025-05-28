package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.Role;
import lombok.Getter;

import java.util.UUID;

@Getter
public class RoleUpdateReqeust {
    private UUID userId;
    private Role newRole;
}
