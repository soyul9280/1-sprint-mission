package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class UserStatusNotFoundByUserId extends UserException{

    public UserStatusNotFoundByUserId(UUID userId) {
        super(ErrorCode.USER_STATUS_NOT_FOUND_BY_USERID, Map.of("userId", userId));
    }
}
