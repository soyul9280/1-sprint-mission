package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatusCreateDto {
    private UUID userId;
    private Instant lastAttendAt;
}
