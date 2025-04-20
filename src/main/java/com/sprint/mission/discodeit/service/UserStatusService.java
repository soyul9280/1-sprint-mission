package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.response.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateDto;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserStatusDto create(UserStatusCreateDto userStatusCreate);
    UserStatusDto findById(UUID id);
    List<UserStatusDto> findAll();
    UserStatusDto updateByUserId(UUID userId, UserStatusUpdateDto updateUserStatus);
    void delete(UUID userStatusId);
}
