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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.UUID;

@Slf4j
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
        log.debug("읽음 상태 생성 시작: userId={}, channelId={}", readStatusParam.getUserId(), readStatusParam.getChannelId());
        User user = userRepository.findById(readStatusParam.getUserId()).orElseThrow(() -> new UserNotFoundException(readStatusParam.getUserId()));
        Channel channel = channelRepository.findById(readStatusParam.getChannelId()).orElseThrow(() -> new ChannelNotFoundException(readStatusParam.getChannelId()));
        if (!readStatusRepository.findByUserIdAndChannelId(user.getId(), channel.getId()).isEmpty()) {
            throw new ReadStatusAlreadyExistException(user.getId(), channel.getId());
        }
        ReadStatus readStatus=new ReadStatus(user,channel);
        readStatusRepository.save(readStatus);
        log.info("읽음 상태 생성 완료: id={},userId={},channelId={}",readStatus.getId(),user.getId(),channel.getId());
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public ReadStatusDto findById(UUID readStatusId) {
        log.debug("읽음 상태 조회 시작: id={}", readStatusId);
        ReadStatus readStatus = readStatusRepository.findById(readStatusId).orElseThrow(() -> new ReadStatusNotFoundException(readStatusId));
        log.info("읽음 상태 조회 완료: id={}", readStatusId);
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    public List<ReadStatusDto> findAllByUserId(UUID userId) {
        log.debug("사용자별 읽음 상태 목록 조회 시작: userId={}", userId);
        List<ReadStatusDto> list = readStatusRepository.findAllByUserId(userId)
                .stream()
                .map(readStatusMapper::toDto)
                .toList();
        log.info("사용자별 읽음 상태 목록 조회 완료: userId={}, 조회된 항목 수={}", userId, list.size());
        return list;
    }

    @Override
    @Transactional
    public ReadStatusDto update(UUID readStatusId, ReadStatusUpdateDto readStatusParam) {
        log.debug("읽음 상태 수정 시작: id={},newLastReadAt={}", readStatusId, readStatusParam.getNewLastReadAt());
        ReadStatus readStatus = readStatusRepository.findById(readStatusId).orElseThrow(()->new ReadStatusNotFoundException(readStatusId));
        readStatus.updateRead(readStatusParam.getNewLastReadAt());
        log.info("읽음 상태 수정 완료: id={}", readStatusId);
        return readStatusMapper.toDto(readStatus);
    }

    @Override
    @Transactional
    public void delete(UUID readStatusId) {
        log.debug("읽음 상태 삭제 시작: id={}", readStatusId);
        ReadStatus readStatus = readStatusRepository.findById(readStatusId).orElseThrow(()->new ReadStatusNotFoundException(readStatusId));
        readStatusRepository.deleteById(readStatus.getId());
        log.info("읽음 상태 삭제 완료: id={}", readStatusId);
    }
}
