package com.sprint.mission.discodeit.event.exception;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

public class NotificationException extends DiscodeitException {
    public NotificationException(ErrorCode code) {
        super(code);
    }
    public NotificationException(ErrorCode code, Throwable cause) {
        super(code, cause);
    }
}
