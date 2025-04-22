package com.sprint.mission.discodeit.exception.user;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class UserAlreadyExistException extends UserException {

    public UserAlreadyExistException(String emailOrUsername) {
        super(ErrorCode.USER_ALREADY_EXISTS, Map.of("emailOrUsername", emailOrUsername));
    }
}
