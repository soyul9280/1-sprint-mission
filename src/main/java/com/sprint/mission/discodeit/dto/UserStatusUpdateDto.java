package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Instant;

@Getter
public class UserStatusUpdateDto {
    @NotNull
    private Instant newAttendAt;
}
