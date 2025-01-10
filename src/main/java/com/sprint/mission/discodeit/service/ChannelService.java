package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.response.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Channel createPublicChannel(PublicChannelCreateRequestDto channel);
    Channel createPrivateChannel(PrivateChannelCreateRequestDto channel);
    List<ChannelResponseDto> findAllChannels();
    List<ChannelResponseDto> findAllByUserId(UUID userId);
    Channel updateChannel(UUID id, ChannelUpdateRequestDto channelParam);
    void deleteChannel(UUID id);
}
