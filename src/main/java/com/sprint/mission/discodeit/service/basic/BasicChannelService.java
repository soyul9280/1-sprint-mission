package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelUpdateForbiddenException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ChannelMapper channelMapper;

    @Override
    @Transactional
    public ChannelDto createPublicChannel(PublicChannelCreateRequestDto channelParam) {
        Channel channel = new Channel(channelParam.getName(), channelParam.getDescription(), ChannelType.PUBLIC);
        channelRepository.save(channel);
        return channelMapper.toDto(channel);
    }


    @Override
    @Transactional
    public ChannelDto createPrivateChannel(PrivateChannelCreateRequestDto channelParam) {
        Channel channel = new Channel(null,null,ChannelType.PRIVATE);
        channelRepository.save(channel);
        List<ReadStatus> readStatuses = userRepository.findAllById(channelParam.getUserIds()).stream()
                .map(user -> new ReadStatus(user, channel))
                .toList();
        readStatusRepository.saveAll(readStatuses);
        return channelMapper.toDto(channel);
    }


    @Override
    public ChannelDto find(UUID channelId) {
        Channel channel = channelRepository.findById(channelId).orElseThrow((() -> new ChannelNotFoundException(channelId)));
        return channelMapper.toDto(channel);
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        List<UUID> channelIds = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannel)
                .map(Channel::getId)
                .toList();

        return channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, channelIds).stream()
                .map(channelMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ChannelDto updateChannel(UUID id, ChannelUpdateRequestDto channelParam) {
        Channel channel = channelRepository.findById(id).orElseThrow(() -> new ChannelNotFoundException(id));
        if(channel.getType()==ChannelType.PRIVATE) {
            throw new ChannelUpdateForbiddenException(channel.getId());
        }
        channel.update(channelParam.getNewName(), channelParam.getNewDescription());
        return channelMapper.toDto(channel);
    }

    @Override
    @Transactional
    public void deleteChannel(UUID channelId) {
        channelRepository.findById(channelId).orElseThrow(() -> new ChannelNotFoundException(channelId));
        messageRepository.deleteByChannelId(channelId);
        readStatusRepository.deleteAllByChannelId(channelId);
        channelRepository.deleteById(channelId);
    }

}
