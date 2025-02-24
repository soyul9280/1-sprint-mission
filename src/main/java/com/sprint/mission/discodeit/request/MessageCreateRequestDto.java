package com.sprint.mission.discodeit.dto.request;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class MessageCreateRequestDto {

    private String content;

    private UUID senderId;

    private UUID channelId;
    private List<UUID> messageFiles;
}
