package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data;

    public JCFChannelService() {
        this.data = new ArrayList<>();
    }

    @Override
    public void create(Channel channel) {
        data.add(channel);
    }

    @Override
    public Optional<Channel> read(UUID id) {
        return data.stream().filter(channel -> channel.getId().equals(id)).findFirst();
    }

    @Override
    public List<Channel> readAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void update(UUID id, Channel updatedChannel) {
        read(id).ifPresent(channel -> {
            channel.updateName(updatedChannel.getName());
            channel.updateDescription(updatedChannel.getDescription());
        });
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(channel -> channel.getId().equals(id));
    }
}
