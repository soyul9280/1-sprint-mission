package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusService {
    UserStatus create(UserStatusCreateDto userStatusCreate);
    Optional<UserStatus> find(UUID userStatusId);
    List<UserStatus> findAll();
    UserStatus updateByUserId(UUID userId, UserStatusUpdateDto updateUserStatus);
    void delete(UUID userStatusId);
}
