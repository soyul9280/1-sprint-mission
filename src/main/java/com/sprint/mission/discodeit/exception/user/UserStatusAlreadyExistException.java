package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class UserStatusAlreadyExistException extends UserException{
    public UserStatusAlreadyExistException(UUID userId) {
        super(ErrorCode.READSTATUS_ALREADY_EXISTS, Map.of("userId", userId));
    }
}
