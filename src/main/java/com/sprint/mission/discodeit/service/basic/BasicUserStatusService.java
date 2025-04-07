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
        User user = userRepository.findById(userStatusCreate.getUserId()).orElseThrow(() -> new UserNotFoundException(userStatusCreate.getUserId()));

        if (userStatusRepository.findByUserId(user.getId()).isPresent()) {
            throw new UserStatusAlreadyExistException(user.getId());
        }

        UserStatus userStatus = new UserStatus();
        userStatus.setUser(user);
        userStatusRepository.save(userStatus);
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public UserStatusDto findById(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id).orElseThrow(() -> new UserStatusNotFoundException(id));
        return userStatusMapper.toDto(userStatus);

    }

    @Override
    public List<UserStatusDto> findAll() {
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toDto)
                .toList();
    }

    @Override
    public UserStatusDto updateByUserId(UUID userId, UserStatusUpdateDto updateUserStatus) {
        Instant newLastAttendAt = updateUserStatus.getNewAttendAt();
        UserStatus userStatus = userStatusRepository.findByUserId(userId).orElseThrow(() -> new UserStatusNotFoundByUserId(userId));
        userStatus.updateUserStatus(newLastAttendAt);
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public void delete(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userStatusRepository.deleteById(userStatus.getId());
    }
}
