package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.NotificationType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.event.notification.NotificationEvent;
import com.sprint.mission.discodeit.event.exception.InvalidNotificationTypeException;
import com.sprint.mission.discodeit.event.exception.NotificationNotFoundException;
import com.sprint.mission.discodeit.event.exception.UnsupportedNotificationTypeException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BasicNotificationService implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;


    @Override
    @Cacheable(value = "userNotifications", key = "#userId")
    public List<NotificationDto> getNotifications(UUID userId) {
        validateUserId(userId);
        List<Notification> notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId);
        return notifications.stream().map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "userNotifications",key = "#userId")
    public void delete(UUID notificationId, UUID userId) {
        validateNotificationId(notificationId);
        validateUserId(userId);
        int deletedCount = notificationRepository.deletedByIdAndReceiverId(notificationId, userId);
        if(deletedCount == 0) {
            throw new IllegalArgumentException("알림을 찾을 수 없거나 삭제 권한이 없습니다.");
        }
    }


    @Override
    public void publishNewMessageNotification(UUID channelId, String title, String content) {
        validateChannelId(channelId);
        try {
            List<ReadStatus> enabledUsers = readStatusRepository.findByChannelIdAndNotificationEnabledTrue(channelId);
            if (enabledUsers.isEmpty()) {
                return;
            }
            for (ReadStatus readStatus : enabledUsers) {
                validateNotificationEvent(NotificationType.NEW_MESSAGE, readStatus.getUser().getId(), channelId);
                eventPublisher.publishEvent(NotificationEvent.newMessage(
                        readStatus.getUser().getId(),
                        channelId,
                        "새 메시지: " + title,
                        content
                ));
            }
        } catch (Exception e) {
            throw new RuntimeException("알림 발행 중 오류가 발생했습니다.", e);
        }
    }


    @Override
    public void publishRoleChangedNotification(UUID userId, Role role) {
        validateUserId(userId);
        validateNotificationEvent(NotificationType.ROLE_CHANGED, userId, userId);
        try {
            eventPublisher.publishEvent(NotificationEvent.roleChanged(userId, userId, "권한 변경: " + role));
        } catch (Exception e) {
            throw new RuntimeException("권한 변경 알림 발행 중 오류가 발생했습니다", e);
        }
    }

    @Override
    public void publishAsyncFailedNotification(UUID userId, String failureMessage) {
        validateUserId(userId);
        validateFailureMessage(failureMessage);
        validateNotificationEvent(NotificationType.ASYNC_FAILED, userId, null);
        try {
            eventPublisher.publishEvent(NotificationEvent.asyncFailed(
                    userId, truncateMessage("비동기 작업 실패: " + failureMessage, 255)
            ));
        } catch (Exception e) {
            throw new RuntimeException("비동기 실패 알림 발행 중 오류가 발생했습니다.",e);
        }
    }

    private String truncateMessage(String message, int maxLength) {
        if (message == null) {
            return "";
        }
        if(message.length() <= maxLength) {
            return message;
        }
        return message.substring(0, maxLength-3) + "...";
    }

    private void validateFailureMessage(String failureMessage) {
        if(failureMessage == null||failureMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("실패 메시지가 비어있습니다.");
        }
        if (failureMessage.length() > 500) {
            throw new IllegalArgumentException("실패 메시지의 최대 길이는 500자 입니다.");
        }
    }

    private void validateUserId(UUID userId) {
        if (userId == null || !userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }
    }

    private void validateNotificationId(UUID notificationId) {
        if(notificationId == null || !notificationRepository.existsById(notificationId)) {
            throw new NotificationNotFoundException();
        }
    }

    private void validateChannelId(UUID channelId) {
        if(channelId == null || !channelRepository.existsById(channelId)) {
            throw new ChannelNotFoundException();
        }

    }

    private void validateNotificationEvent(NotificationType type, UUID receiverId, UUID targetId) {
        if (type == null) {
            throw new InvalidNotificationTypeException();
        }
        if(receiverId == null || !userRepository.existsById(receiverId)) {
            throw new UserNotFoundException();
        }
        switch (type) {
            case NEW_MESSAGE:
                if (targetId == null || !channelRepository.existsById(targetId)) {
                    throw new ChannelNotFoundException().withId(targetId);
                }
                break;
            case ROLE_CHANGED:
                if(targetId == null || !channelRepository.existsById(targetId)) {
                    throw new ChannelNotFoundException().withId(targetId);
                }
                break;
            case ASYNC_FAILED:
                break;
            default:
                throw new UnsupportedNotificationTypeException().wrongType(type);
        }
    }

}
