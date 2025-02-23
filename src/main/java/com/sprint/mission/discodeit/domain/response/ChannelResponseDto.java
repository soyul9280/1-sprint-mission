package com.sprint.mission.discodeit.dto.response;


import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class ChannelResponseDto {
    private UUID id;

    private String channelName;
    private String description;
    List<UUID> participantIds;
    private Instant lastMessageTime;

    public ChannelResponseDto(String channelName, String description, List<UUID> participantIds, Instant lastMessageTime) {
        this.channelName = channelName;
        this.description = description;
        this.participantIds = participantIds;
        this.lastMessageTime = lastMessageTime;
    }
}
