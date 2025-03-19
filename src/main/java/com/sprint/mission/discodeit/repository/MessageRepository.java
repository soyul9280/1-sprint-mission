package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    @EntityGraph(attributePaths = {"channel","author","attachments"})
    Slice<Message> findAllByChannelId(UUID channelId, Pageable pageable);

    void deleteByChannelId(UUID channelId);
}
