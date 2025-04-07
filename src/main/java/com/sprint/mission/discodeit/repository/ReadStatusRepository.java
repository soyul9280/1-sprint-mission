package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {
    List<ReadStatus> findAllByUserId(UUID userId);

    //고민하기
    @Query("select r from ReadStatus r" +
            " join fetch r.user u" +
            " join fetch  u.status" +
            " left join fetch u.profile" +
            " where r.channel.id = :channelId")
    List<ReadStatus> findAllByChannelIdWithUser(@Param("channelId") UUID channelId);
    List<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

    void deleteAllByChannelId(UUID channelId);
}
