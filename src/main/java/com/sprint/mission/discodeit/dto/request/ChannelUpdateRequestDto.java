package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChannelUpdateRequestDto {

    @NotBlank(message = "채널이름은 필수입니다.")
    private String newName;

    @NotBlank(message = "채널 설명을 입력해주세요.")
    private String newDescription;
}
