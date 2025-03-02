package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.ChannelGroup;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class PrivateChannelCreateRequestDto {
    private List<UUID> userIds;
    private ChannelGroup group;
}
