package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.ChannelGroup;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


@Getter
public class PublicChannelCreateRequestDto {
    @NotBlank
    private String channelName;
    private String description;
    private ChannelGroup group;
}
