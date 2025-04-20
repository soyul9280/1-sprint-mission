package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MessageUpdateDto {
    @NotBlank(message = "메세지 내용은 필수입니다.")
    private String newContent;
}
