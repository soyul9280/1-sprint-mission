package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.docs.ReadStatusApi;
import com.sprint.mission.discodeit.dto.request.ReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.dto.response.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.mapper.ReadStatusMapper;
import com.sprint.mission.discodeit.service.ReadStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusApiController implements ReadStatusApi {
    private final ReadStatusService readStatusService;


    @GetMapping
    @Override
    public ResponseEntity<List<ReadStatusDto>> getUserReadStatus(@RequestParam("userId") UUID userId) {
        log.info("사용자별 읽음 상태 목록 조회 요청: userId={}", userId);
        List<ReadStatusDto> result = readStatusService.findAllByUserId(userId);
        log.debug("사용자별 읽음 상태 목록 조회 응답: count={}",result.size());
        return ResponseEntity.ok(result);
    }


    @PostMapping
    @Override
    public ResponseEntity<ReadStatusDto> createUserReadStatus(@Valid @RequestBody ReadStatusRequestDto request) {
        log.info("읽음 상태 생성 요청: {}", request);
        ReadStatusDto result = readStatusService.create(request);
        log.debug("읽음 상태 생성 응답:{}", result);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusDto> updateUserReadStatus(@PathVariable("readStatusId") UUID readStatusId,@Valid @RequestBody ReadStatusUpdateDto request) {
        log.info("읽음 상태 수정 요청: id={},request={}", readStatusId, request);
        ReadStatusDto result = readStatusService.update(readStatusId, request);
        log.debug("읽음 상태 수정 응답: {}",result);
        return ResponseEntity.ok(result);
    }
}
