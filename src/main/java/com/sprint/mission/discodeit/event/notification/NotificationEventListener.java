package com.sprint.mission.discodeit.event.notification;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {
    private final NotificationRepository notificationRepository;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void handleNotification(NotificationEvent event) {
        try{
            Notification notification = new Notification(
                    event.getReceiverId(),
                    event.getType(),
                    event.getTargetId(),
                    event.getTitle(),
                    event.getContent());
            notificationRepository.save(notification);
        }catch (Exception e){
            throw e;
        }
    }

    @Recover
    public void recover(Exception e, NotificationEvent event) {
        log.error("알림 이벤트 모든 재시도 실패 : {}, 오류: {}",event,e.getMessage());
    }
}
