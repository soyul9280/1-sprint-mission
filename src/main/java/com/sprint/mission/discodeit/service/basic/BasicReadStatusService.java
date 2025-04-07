package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusAlreadyExistException;
import com.sprint.mission.discodeit.exception.readstatus.ReadStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusMapper readStatusMapper;

    @Override
    @Transactional
    public ReadStatusDto create(ReadStatusRequestDto readStatusParam) {
        User user = userRepository.findById(readStatusParam.getUserId()).orElseThrow(() -> new UserNotFoundException(readStatusParam.getUserId()));
        Channel channel = channelRepository.findById(readStatusParam.getChannelId()).orElseThrow(() -> new ChannelNotFoundException(readStatusParam.getChannelId()));
        if (!readStatusRepository.findByUserIdAndChannelId(user.getId(), channel.getId()).isEmpty()) {
            throw new ReadStatusAlreadyExistException(user.getId(), channel.getId());
        }
        ReadStatus readStatus=new ReadStatus(user,channel);
        readStatusRepository.save(readStatus);
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public ReadStatusDto findById(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId).orElseThrow(() -> new ReadStatusNotFoundException(readStatusId));
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId)
                .stream()
                .map(readStatusMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateDto readStatusParam) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId).orElseThrow(()->new ReadStatusNotFoundException(readStatusId));
        readStatus.updateRead(readStatusParam.getNewLastReadAt());
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    @Transactional
    public void delete(UUID readStatusId) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId).orElseThrow(()->new ReadStatusNotFoundException(readStatusId));
        readStatusRepository.deleteById(readStatus.getId());
    }
}
