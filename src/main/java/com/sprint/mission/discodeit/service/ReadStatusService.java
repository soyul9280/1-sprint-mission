package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ReadStatusRequestDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatus create(ReadStatusRequestDto readStatus);

    Optional<ReadStatus> find(UUID readStatusId);

    List<ReadStatus> findAllByUserId(UUID userId);

    ReadStatus update(UUID readStatusId, ReadStatusUpdateDto readStatusUpdate);
    void delete(UUID readStatusId);
}
