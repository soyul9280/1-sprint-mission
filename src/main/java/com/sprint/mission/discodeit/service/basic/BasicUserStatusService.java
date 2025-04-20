package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import com.sprint.mission.discodeit.dto.request.UserStatusUpdateDto;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserStatusAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserStatusNotFoundByUserId;
import com.sprint.mission.discodeit.exception.user.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    @Transactional
    public UserStatusDto create(UserStatusCreateDto userStatusCreate) {
        log.debug("사용자 상태 생성 시작: userId={}", userStatusCreate.getUserId());
        User user = userRepository.findById(userStatusCreate.getUserId()).orElseThrow(() -> new UserNotFoundException(userStatusCreate.getUserId()));

        if (userStatusRepository.findByUserId(user.getId()).isPresent()) {
            throw new UserStatusAlreadyExistException(user.getId());
        }

        UserStatus userStatus = new UserStatus();
        userStatus.setUser(user);
        userStatusRepository.save(userStatus);
        log.info("사용자상태 생성 완료: id={},userId={}", userStatus.getId(), userStatus.getUser().getId());
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public UserStatusDto findById(UUID id) {
        log.debug("사용자 상태 조회 시작: id={}", id);
        UserStatus userStatus = userStatusRepository.findById(id).orElseThrow(() -> new UserStatusNotFoundException(id));
        log.info("사용자 상태 조회 완료: id={}", userStatus.getUser().getId());
        return userStatusMapper.toDto(userStatus);

    }

    @Override
    public List<UserStatusDto> findAll() {
        log.debug("전체 사용자 상태 목록 조회 시작");
        List<UserStatusDto> list = userStatusRepository.findAll().stream()
                .map(userStatusMapper::toDto)
                .toList();
        log.info("현재 사용자 상태 목록 조회 완료: 조회된 항목 수={}", list.size());
        return list;
    }

    @Override
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateDto updateUserStatus) {
        log.debug("사용자 상태 수정 시작: userId={},newLastActiveAt={}", userId, updateUserStatus.getNewLastActiveAt());
        Instant newLastAttendAt = updateUserStatus.getNewLastActiveAt();
        UserStatus userStatus = userStatusRepository.findByUserId(userId).orElseThrow(() -> new UserStatusNotFoundByUserId(userId));
        userStatus.updateUserStatus(newLastAttendAt);
        log.info("사용자 상태 수정 완료: userId={}",userId);
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public void delete(UUID id) {
        log.debug("사용자 상태 삭제 시작: id={}", id);
        UserStatus userStatus = userStatusRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userStatusRepository.deleteById(userStatus.getId());
        log.info("사용자 상태 삭제 완료:id={}", id);
    }
}
