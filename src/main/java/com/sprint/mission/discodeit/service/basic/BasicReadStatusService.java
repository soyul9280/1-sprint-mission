package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ReadStatusRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicReadStatusService implements ReadStatusService {
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    @Transactional
    public ReadStatus create(ReadStatusRequestDto readStatusParam) {
        User user = userRepository.findById(readStatusParam.getUserId()).orElseThrow(() -> new IllegalArgumentException("해당 User가 존재하지 않습니다."));
        Channel channel = channelRepository.findById(readStatusParam.getChannelId()).orElseThrow(() -> new IllegalArgumentException("해당 Channel이 존재하지 않습니다."));
        if (!readStatusRepository.findByUserIdAndChannelId(user.getId(), channel.getId()).isEmpty()) {
            throw new IllegalArgumentException("해당 유저의 읽음 상태가 이미 존재합니다.");
        }
        ReadStatus readStatus=new ReadStatus(user,channel);
        return readStatusRepository.save(readStatus);
    }

    @Override
    public Optional<ReadStatus> find(UUID readStatusId) {
        return readStatusRepository.findById(readStatusId);
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return readStatusRepository.findAllByUserId(userId);
    }

    @Override
    @Transactional
    public ReadStatus update(UUID readStatusId, ReadStatusUpdateDto readStatusParam) {
        ReadStatus readStatus = readStatusRepository.findById(readStatusId).orElseThrow(()->new IllegalArgumentException("해당 ID의 ReadStatus가 존재하지 않습니다."));
        readStatus.updateRead(readStatusParam.getNewLastReadAt());
        return readStatus;
    }

    @Override
    @Transactional
    public void delete(UUID readStatusId) {
        readStatusRepository.deleteById(readStatusId);
    }
}
