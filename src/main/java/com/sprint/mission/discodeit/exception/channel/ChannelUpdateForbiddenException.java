package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

public class ChannelUpdateForbiddenException extends ChannelException{
    public ChannelUpdateForbiddenException(UUID channelId) {
        super(ErrorCode.CHANNEL_UPDATE_FORBIDDEN, Map.of("channelId", channelId));
    }
}
