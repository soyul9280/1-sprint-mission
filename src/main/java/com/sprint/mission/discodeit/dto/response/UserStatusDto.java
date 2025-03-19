package com.sprint.mission.discodeit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class UserStatusDto {
    private UUID id;
    private UUID userId;
    private Instant lastActiveAt;
}
