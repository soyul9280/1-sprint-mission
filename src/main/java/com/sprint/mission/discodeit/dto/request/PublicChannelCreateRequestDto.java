package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class PublicChannelCreateRequestDto {

    @NotBlank(message = "채널 이름은 필수입니다.")
    private String name;
    @NotBlank(message = "채널 설명을 입력해주세요.")
    private String description;
}
