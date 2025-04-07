package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class LoginUserNotFoundException extends AuthException{
    public LoginUserNotFoundException(String username) {
        super(ErrorCode.LOGIN_USER_NOT_FOUND, Map.of("username", username));
    }
}
