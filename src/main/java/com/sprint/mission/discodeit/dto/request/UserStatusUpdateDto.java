package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class UserStatusUpdateDto {
    @NotNull(message = "새로운 활동 시간은 필수입니다.")
    private Instant newAttendAt;
}
