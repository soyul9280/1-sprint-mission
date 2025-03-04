package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
public class JcfChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;

    public JcfChannelRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel createChannel(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel updateChannel(UUID id, ChannelUpdateRequestDto channelUpdateDto) {
        Channel findChannel = findById(id).orElseThrow(() -> new NoSuchElementException("Channel not found"));
        if (!findChannel.isPublic(findChannel)){
            throw new IllegalStateException("PRIVATE는 채널 수정 불가입니다");
        }
        findChannel.update(channelUpdateDto.getNewChannelName(), channelUpdateDto.getNewDescription());
        return createChannel(findChannel);
    }

    @Override
    public void deleteChannel(UUID id) {
        data.remove(id);
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<Channel> findByChannelName(String channelName) {
        List<Channel> all=findAll();
        for (Channel channel : all) {
            if(channel.getChannelName().equals(channelName)) {
                return Optional.of(channel);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

}
