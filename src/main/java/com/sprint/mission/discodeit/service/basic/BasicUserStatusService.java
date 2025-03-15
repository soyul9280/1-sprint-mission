package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import com.sprint.mission.discodeit.dto.request.UserStatusUpdateDto;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserStatus create(UserStatusCreateDto userStatusCreate) {
        User user = userRepository.findById(userStatusCreate.getUserId()).orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));
        if(userStatusRepository.findByUserId(userStatusCreate.getUserId()).isPresent()) {
            throw new IllegalArgumentException("이미 해당 유저가 존재합니다.");
        }
        UserStatus userStatus = new UserStatus();
        return userStatusRepository.save(userStatus);
    }

    @Override
    public Optional<UserStatus> find(UUID userStatusId) {
        return userStatusRepository.findById(userStatusId);
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateDto updateUserStatus) {
        Instant newLastAttendAt = updateUserStatus.getNewAttendAt();
        UserStatus userStatus = userStatusRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("해당 유저 상태가 존재하지 않습니다."));
        userStatus.updateUserStatus(newLastAttendAt);
        return userStatus;
    }

    @Override
    public void delete(UUID userStatusId) {
        userStatusRepository.deleteById(userStatusId);
    }
    public boolean isUserOnline(UUID userId) {
        if (userStatusRepository.findByUserId(userId).isEmpty()) {
            return false;
        } else {
            UserStatus userStatus = userStatusRepository.findByUserId(userId).get();
            if(userStatus.isOnline()) {
                return true;
            }
            return false;
        }
    }

    public List<UUID> getOnlineUsers() {
        List<UserStatus> all = userStatusRepository.findAll();
        List<UUID> userIds = new ArrayList<>();
        for (UserStatus userStatus : all) {
            if(!userStatus.isOnline()) {
                log.info("오프라인");
            }
            userIds.add(userStatus.getUser().getId());
        }
        return userIds;
    }
}
