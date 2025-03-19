package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class PrivateChannelCreateRequestDto {
    @NotEmpty(message = "참가자항목은 필수입니다.")
    private List<UUID> userIds;
}
