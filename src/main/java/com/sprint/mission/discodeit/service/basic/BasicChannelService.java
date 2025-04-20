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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
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
        log.debug("채널 생성 시작: {}", channelParam);
        Channel channel = new Channel(channelParam.getName(), channelParam.getDescription(), ChannelType.PUBLIC);
        channelRepository.save(channel);
        log.info("채널 생성 완료: id={}, name={}", channel.getId(), channel.getName());
        return channelMapper.toDto(channel);
    }

    @Override
    @Transactional
    public ChannelDto createPrivateChannel(PrivateChannelCreateRequestDto channelParam) {
        log.debug("채널 생성 시작: {}", channelParam);
        Channel channel = new Channel(null,null,ChannelType.PRIVATE);
        channelRepository.save(channel);
        List<ReadStatus> readStatuses = userRepository.findAllById(channelParam.getParticipantIds()).stream()
                .map(user -> new ReadStatus(user, channel))
                .toList();
        readStatusRepository.saveAll(readStatuses);
        log.info("채널 생성 완료: id={}, name={}", channel.getId(), channel.getName());
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
        log.debug("채널 수정 시작: id={},request={}", id, channelParam);
        Channel channel = channelRepository.findById(id).orElseThrow(() -> new ChannelNotFoundException(id));
        if(channel.getType()==ChannelType.PRIVATE) {
            throw new ChannelUpdateForbiddenException(channel.getId());
        }
        channel.update(channelParam.getNewName(), channelParam.getNewDescription());
        log.info("채널 수정 완료: id={},name={}", id, channel.getName());
        return channelMapper.toDto(channel);
    }

    @Override
    @Transactional
    public void deleteChannel(UUID channelId) {
        log.debug("채널 삭제 시작: id={}", channelId);
        channelRepository.findById(channelId).orElseThrow(() -> new ChannelNotFoundException(channelId));
        messageRepository.deleteByChannelId(channelId);
        readStatusRepository.deleteAllByChannelId(channelId);
        channelRepository.deleteById(channelId);
        log.info("채널 삭제 완료: id={}", channelId);
    }

}
