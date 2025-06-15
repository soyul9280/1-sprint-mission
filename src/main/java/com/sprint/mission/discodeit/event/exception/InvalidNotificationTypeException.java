package com.sprint.mission.discodeit.event.exception;

import com.sprint.mission.discodeit.exception.ErrorCode;

public class InvalidNotificationTypeException extends NotificationException {
    public InvalidNotificationTypeException() {
        super(ErrorCode.INVALID_NOTIFICATION_TYPE);
    }

}

