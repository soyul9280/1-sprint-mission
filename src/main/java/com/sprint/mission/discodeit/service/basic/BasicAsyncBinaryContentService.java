package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.AsyncTaskFailure;
import com.sprint.mission.discodeit.entity.UploadStatus;
import com.sprint.mission.discodeit.repository.AsyncTaskFailureRepository;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.AsyncBinaryContentService;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class BasicAsyncBinaryContentService implements AsyncBinaryContentService {
    private final BinaryContentRepository binaryContentRepository;
    private final AsyncTaskFailureRepository asyncTaskFailureRepository;
    private final BinaryContentStorage binaryContentStorage;

    @Async("taskExecutor")
    @Retryable(
            retryFor = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000,multiplier = 2)
    )
    @Timed(value = "async.file.upload",description = "비동기 파일 업로드 시간")
    @Override
    public CompletableFuture<Void> uploadFileAsync(UUID binaryContentId, byte[] fileData) {
        String requestId = MDC.get("requestId");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return CompletableFuture.runAsync(()->{
            try{
                MDC.put("requestId", requestId);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                Thread.sleep(2000);
                if (binaryContentId.toString().startsWith("ffffffff")) {
                    throw new RuntimeException("재시도 메커니즘 테스트용 예외");
                }
                binaryContentStorage.put(binaryContentId, fileData);
                updateBinaryContentStatus(binaryContentId, UploadStatus.SUCCESS);
            }catch (Exception ex){
                throw new RuntimeException("파일 업로드 실패",ex);
            }finally {
                MDC.clear();
                SecurityContextHolder.clearContext();
            }
        });
    }

    @Override
    @Recover
    public CompletableFuture<Void> recoverFromUploadFailure(Exception ex, UUID binaryContentId, byte[] fileData) {
        String requestId = MDC.get("requestId");
        AsyncTaskFailure failure = new AsyncTaskFailure("FILE_UPLOAD", requestId, ex.getMessage(), binaryContentId, 3);
        asyncTaskFailureRepository.save(failure);
        updateBinaryContentStatus(binaryContentId, UploadStatus.FAILED);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Transactional
    public void updateBinaryContentStatus(UUID binaryContentId, UploadStatus status) {
        binaryContentRepository.findById(binaryContentId).ifPresent(binaryContent->{
            binaryContent.setUploadStatus(status);
            binaryContentRepository.save(binaryContent);
        });
    }
}
