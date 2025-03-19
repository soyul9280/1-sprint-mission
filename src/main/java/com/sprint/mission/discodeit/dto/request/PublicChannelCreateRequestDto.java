package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


@Getter
public class PublicChannelCreateRequestDto {
    @NotBlank(message = "채널 이름은 필수입니다.")
    private String name;
    private String description;
}
