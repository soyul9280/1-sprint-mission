package com.sprint.mission.discodeit.entity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    void create(Channel channel);
    Optional<Channel> read(UUID id);
    List<Channel> readAll();
    void update(UUID id, Channel channel);
    void delete(UUID id);
}
