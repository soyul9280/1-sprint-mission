package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByReceiverIdOrderByCreatedAtDesc(UUID receiverId);

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.id=:notificationId AND n.receiverId=:receiverId")
    int deletedByIdAndReceiverId(@Param("notificationId") UUID notificationId, @Param("receiverId") UUID receiverId);
}
