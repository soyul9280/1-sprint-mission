package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.AsyncTaskFailure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AsyncTaskFailureRepository extends JpaRepository<AsyncTaskFailure, String> {
    List<AsyncTaskFailure> findByRequestId(String requestId);
    List<AsyncTaskFailure> findByBinaryContentId(UUID binaryContentId);
    List<AsyncTaskFailure> findByTaskName(String taskName);

}
