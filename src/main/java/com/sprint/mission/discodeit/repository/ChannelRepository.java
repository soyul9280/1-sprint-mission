package com.sprint.mission.discodeit.repository;


import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ChannelRepository {
    Channel createChannel(Channel channel);
    Channel updateChannel(UUID id, ChannelUpdateRequestDto channelUpdateDto);
    void deleteChannel(UUID id);
    Optional<Channel> findById(UUID id);
    Optional<Channel> findByChannelName(String channelName);
    List<Channel> findAll();
}
