package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageCreateRequestDto {

    @NotBlank(message = "메세지 내용입력은 필수입니다.")
    private String content;

    @NotNull(message = "보내는 사람 ID는 필수입니다.")
    private UUID senderId;

    @NotNull(message = "채널 ID는 필수입니다.")
    private UUID channelId;
    private List<UUID> messageFiles;
}
