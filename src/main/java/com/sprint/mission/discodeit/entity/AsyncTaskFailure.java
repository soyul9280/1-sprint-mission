package com.sprint.mission.discodeit.entity;


import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Getter
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
public class AsyncTaskFailure extends BaseUpdatableEntity {
    private String taskName;
    private String requestId;
    private String failureReason;
    private UUID binaryContentId;
    private Integer retryCount;

}
