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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/readStatuses")
public class ReadStatusApiController implements ReadStatusApi {
    private final ReadStatusService readStatusService;


    @GetMapping
    @Override
    public ResponseEntity<List<ReadStatusDto>> getUserReadStatus(@RequestParam("userId") UUID userId) {
        List<ReadStatusDto> result = readStatusService.findAllByUserId(userId);
        return ResponseEntity.ok(result);
    }


    @PostMapping
    @Override
    public ResponseEntity<ReadStatusDto> createUserReadStatus(@Valid @RequestBody ReadStatusRequestDto request) {
        ReadStatusDto result = readStatusService.create(request);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{readStatusId}")
    public ResponseEntity<ReadStatusDto> updateUserReadStatus(@PathVariable("readStatusId") UUID readStatusId,@Valid @RequestBody ReadStatusUpdateDto request) {
        ReadStatusDto result = readStatusService.update(readStatusId, request);
        return ResponseEntity.ok(result);
    }
}
