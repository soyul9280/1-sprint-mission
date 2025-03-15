package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    @Query("select c from Channel c " +
            "left join fetch c.participants p " +
            "left join fetch p.user")
    List<Channel> findAll();
    Optional<Channel> findByName(String name);
}
