package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> find() {
        UUID userId = getCurrentUserId();
        List<NotificationDto> notifications=notificationService.getNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> delete(@PathVariable UUID notificationId) {
        UUID userId = getCurrentUserId();
        notificationService.delete(notificationId, userId);
        return ResponseEntity.noContent().build();
    }

    private UUID getCurrentUserId() {
        return ((DiscodeitUserDetails) SecurityContextHolder.getContext().
                getAuthentication().getPrincipal()).getUserDto().id();
    }
}
