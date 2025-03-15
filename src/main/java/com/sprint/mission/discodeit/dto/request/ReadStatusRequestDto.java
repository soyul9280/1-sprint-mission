package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatusRequestDto {
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;
}
